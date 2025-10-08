package de.muenchen.eh.kvue.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.EfileConstants;
import de.muenchen.eh.kvue.claim.efile.ExchangeBuilder;
import de.muenchen.eh.kvue.claim.efile.OpenApiParameterExtractor;
import de.muenchen.eh.kvue.claim.efile.properties.AuthentificationProperties;
import de.muenchen.eh.kvue.claim.efile.properties.ConnectionProperties;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OperationIdFactory {

    private final Environment environment;
    private final CamelContext camelContext;
    private final AuthentificationProperties authentificationProperties;
    private final ConnectionProperties connectionProperties;

    private static final String EAKTE_FILE_PLAN = "eakte.einzelakten.";

    public Exchange createExchange(OperationId operationId, Exchange exchange) {

        Exchange eakteExchange;
        switch (operationId) {
            case READ_APENTRY_COLLECTION -> {
                eakteExchange = createExchangeApentryCollections();
            }
            case READ_APENTRY_CASE_FILES -> {
                ClaimProcessingContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
                eakteExchange = createExchangeApentryCaseFiles(dataWrapper.getEakte());
            }
            case CREATE_FILE ->  {
                ClaimProcessingContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
                Objektreferenz caseFilesCollection = (Objektreferenz) dataWrapper.getEakte().get(OperationId.READ_APENTRY_COLLECTION.name());
                CreateFileDTO caseFile = (CreateFileDTO) dataWrapper.getEakte().get(EfileConstants.CASE_FILE_DTO);
                eakteExchange = createExchangeCaseFile(caseFilesCollection, caseFile);
            }
            default -> {
                eakteExchange = new DefaultExchange(camelContext);
            }
        }

        eakteExchange.getMessage().setHeader(Constants.OPERATION_ID, operationId.getDescriptor());
        eakteExchange.setProperty(Constants.CLAIM, exchange.getProperty(Constants.CLAIM));

        return ExchangeBuilder.create(eakteExchange, operationId.getDescriptor()).withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword()).withRequestValidation(true).build();

    }

       private Exchange createExchangeCaseFile(Objektreferenz apentryReference, CreateFileDTO createFileDTO) {

        Map<String, Object> params = enrichParameterValues(OperationId.CREATE_FILE.getDescriptor());
        Exchange exchange = new DefaultExchange(camelContext);

        params.forEach((key, value) ->
                exchange.getMessage().setHeader(key, value)
        );

        exchange.getMessage().setBody(createFileDTO);
        return exchange;

    }

    private Exchange createExchangeApentryCollections() {

         return createExchange(OperationId.READ_APENTRY_COLLECTION.getDescriptor());
    }

    private Exchange createExchangeApentryCaseFiles(Map<String, Object> eakte) {

        Exchange exchange = createExchange(OperationId.READ_APENTRY_CASE_FILES.getDescriptor());
        Objektreferenz apentryReference = (Objektreferenz) eakte.get(OperationId.READ_APENTRY_COLLECTION.name());
        exchange.getMessage().setHeader("objaddress", apentryReference.getObjaddress());
        exchange.getMessage().setBody(eakte.get(EfileConstants.CASE_FILE_DTO));
        return exchange;
    }

    private Exchange createExchange(String operationId) {

        Map<String, Object> params = enrichParameterValues(operationId);

        Exchange exchange = new DefaultExchange(camelContext);
        params.forEach((key, value) ->
                exchange.getMessage().setHeader(key, value)
        );
        return exchange;
    }

    private Map<String, Object> enrichParameterValues(String operationId) {
        Map<String, Object> params = new HashMap<>();

        OpenApiParameterExtractor extractor = new OpenApiParameterExtractor("openapi/dmsresteai-openapi.json");
        var readApentryParameters = extractor.getParameterNamesForOperation(operationId);

        readApentryParameters.forEach(param -> {
            Optional<String> paramValue = Optional.ofNullable(environment.getProperty(EAKTE_FILE_PLAN.concat(param.replace("-",""))));
            paramValue.ifPresent(value -> params.put(param, value));
        });
        return params;
    }

}
