package de.muenchen.eh.kvue.claim.eakte;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.Claim;
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
public class EakteOperationIdFactory {

    private final Environment environment;
    private final CamelContext camelContext;
    private final EakteObjectProperties eakteObjectProperties;
    private final EakteConnectionProperties connectionProperties;

    private static final String EAKTE_FILE_PLAN = "eakte.object.";

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
        exchange.setProperty(Constants.CLAIM, relatedClaim);
        return EakteExchangeBuilder.create(exchange, operationId.getDescriptor()).withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword()).withRequestValidation(true).build();

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
