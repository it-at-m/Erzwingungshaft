package de.muenchen.eh.kvue.claim.eakte.operation;

import de.muenchen.eh.kvue.claim.eakte.ExchangeBuilder;
import de.muenchen.eh.kvue.claim.eakte.OpenApiParameterExtractor;
import de.muenchen.eh.kvue.claim.eakte.properties.AktenplanEinzelaktenProperties;
import de.muenchen.eh.kvue.claim.eakte.properties.ConnectionProperties;
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
    private final AktenplanEinzelaktenProperties aktenplanEinzelaktenProperties;
    private final ConnectionProperties connectionProperties;

    private static final String EAKTE_FILE_PLAN = "eakte.einzelakten.";

    public Exchange createExchange(OperationId operationId, Object relatedClaim) {

        Exchange exchange;
        switch (operationId) {
            case READ_APENTRY -> {
                exchange = createExchangeReadApentry();
                break;
            }
            default -> {
                exchange = new DefaultExchange(camelContext);
                break;
            }
        }

        exchange.getMessage().setHeader(Constants.OPERATION_ID, operationId);
        exchange.setProperty(Constants.CLAIM, relatedClaim);

        return ExchangeBuilder.create(exchange, operationId.getDescriptor()).withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword()).withRequestValidation(true).build();

    }

    private Exchange createExchangeReadApentry() {

        Map<String, Object> params = enrichParameterValues(OperationId.READ_APENTRY.getDescriptor());

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
