package de.muenchen.eh.kvue.claim.efile.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.ExchangeBuilder;
import de.muenchen.eh.kvue.claim.efile.OpenApiParameterExtractor;
import de.muenchen.eh.kvue.claim.efile.operation.contentbuilder.FileDTOBuilder;
import de.muenchen.eh.kvue.claim.efile.operation.contentbuilder.OutgoingAnfrageDTOBuilder;
import de.muenchen.eh.kvue.claim.efile.operation.contentbuilder.OutgoingRequestBodyDTOBuilder;
import de.muenchen.eh.kvue.claim.efile.operation.contentbuilder.ProcedureDTOBuilder;
import de.muenchen.eh.kvue.claim.efile.properties.ConnectionProperties;
import de.muenchen.eh.kvue.claim.efile.properties.FineProperties;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NotImplementedException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperationIdFactory {

    private static final String EFILE_CASE_FILE_PROPERTIES = "efile.case-file.";

    private final Environment environment;
    private final CamelContext camelContext;
    private final ConnectionProperties connectionProperties;
    private final FineProperties fineProperties;

    private final ClaimDocumentRepository claimDocumentRepository;

    public Exchange createExchange(OperationId operationId, Exchange exchange) {

        Exchange efileExchange;
        switch (operationId) {
            case READ_COLLECTIONS -> {
                efileExchange = createExchange(OperationId.READ_COLLECTIONS.getDescriptor());
            }
            case CREATE_FILE -> {
                efileExchange = createExchangeCaseFile(exchange.getMessage().getBody(ClaimProcessingContentWrapper.class));
            }
            case UPDATE_SUBJECT_DATA -> {
                efileExchange = createExchange(OperationId.UPDATE_SUBJECT_DATA.getDescriptor());
            }
            case CREATE_FINE -> {
                efileExchange = createExchangeFine(exchange.getMessage().getBody(ClaimProcessingContentWrapper.class));
            }
            case CREATE_OUTGOING -> {
                efileExchange = createExchangeOutgoing(exchange.getMessage().getBody(ClaimProcessingContentWrapper.class));
            }
            case CREATE_CONTENT_OBJECT -> {
                efileExchange = createExchangeContentObject();
            }
            default -> {
                efileExchange = new DefaultExchange(camelContext);
            }
        }

        efileExchange.getMessage().setHeader(Constants.OPERATION_ID, operationId.getDescriptor());
        efileExchange.setProperty(Constants.CLAIM, exchange.getProperty(Constants.CLAIM));

        return ExchangeBuilder.create(efileExchange, operationId.getDescriptor()).withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword()).withRequestValidation(true).build();
    }

    private Exchange createExchangeContentObject() {
        Exchange exchange = createExchange(OperationId.CREATE_CONTENT_OBJECT.getDescriptor());
        exchange.getMessage().setBody(null);

        exchange.setException(new NotImplementedException("BePo receipt filing not implemented yet."));

        return exchange;
    }

    private Exchange createExchangeOutgoing(ClaimProcessingContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.CREATE_OUTGOING.getDescriptor());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "multipart/form-data");
        Optional<List<ClaimDocument>> documents = Optional.ofNullable(claimDocumentRepository.findByClaimImportId(dataWrapper.getClaimImport().getId()));
        log.debug("Process outgoing geschaeftspartnerId : {} ", dataWrapper.getClaim().getGeschaeftspartnerId());

        try {

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            ObjectMapper objectMapper = new ObjectMapper();
            builder.addTextBody("params", OutgoingAnfrageDTOBuilder.create(fineProperties, dataWrapper).buildAsJson(), ContentType.APPLICATION_JSON);

            var docs = OutgoingRequestBodyDTOBuilder.create(documents).build();
            docs.forEach(file -> {
                builder.addBinaryBody("giattachmenttype", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            });

            var entity = builder.build();
            exchange.getMessage().setBody(entity.getContent());
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, entity.getContentType());


        } catch (IOException e) {
            exchange.setException(e);
        }

        return exchange;
    }

    private Exchange createExchangeFine(ClaimProcessingContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.CREATE_FINE.getDescriptor());
        exchange.getMessage().setBody(ProcedureDTOBuilder.create(fineProperties, dataWrapper).build());
        return exchange;
    }

    private Exchange createExchangeCaseFile(ClaimProcessingContentWrapper dataWrapper) {

        Exchange exchange = createExchange(OperationId.CREATE_FILE.getDescriptor());
        exchange.getMessage().setBody(FileDTOBuilder.create(dataWrapper).build());
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

        OpenApiParameterExtractor extractor = new OpenApiParameterExtractor(connectionProperties.getEakteApiVersion());
        var readApentryParameters = extractor.getParameterNamesForOperation(operationId);

        readApentryParameters.forEach(param -> {
            Optional<String> paramValue = Optional.ofNullable(environment.getProperty(EFILE_CASE_FILE_PROPERTIES.concat(param.replace("-", ""))));
            paramValue.ifPresent(value -> params.put(param, value));
        });
        return params;
    }

}
