package de.muenchen.eh.infrastructure.integration.efile.operation;

import de.muenchen.eh.infrastructure.integration.efile.ExchangeBuilder;
import de.muenchen.eh.infrastructure.integration.efile.OpenApiParameterExtractor;
import de.muenchen.eh.infrastructure.integration.efile.properties.ConnectionProperties;
import de.muenchen.eh.infrastructure.log.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class OperationIdFactory {

    private static final String EFILE_CASE_FILE_PROPERTIES = "efile.case-file.";

    private final Environment environment;
    private final CamelContext camelContext;
    private final ConnectionProperties connectionProperties;

    protected Map<OperationId, Function<Object, Exchange>> operationIdHandlers;

    public Exchange createExchange(OperationId operationId, Exchange exchange) {

        Exchange efileExchange = operationIdHandlers.get(operationId)
                .apply(exchange.getMessage().getBody());

        efileExchange.getMessage().setHeader(Constants.OPERATION_ID, operationId.getDescriptor());
        efileExchange.setProperty(Constants.CLAIM, exchange.getProperty(Constants.CLAIM));

        return ExchangeBuilder.create(efileExchange, operationId.getDescriptor())
                .withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword())
                .withRequestValidation(true)
                .build();
    }

    protected Exchange createExchange(String operationId) {

        Map<String, Object> params = enrichParameterValues(operationId);

        Exchange exchange = new DefaultExchange(camelContext);
        params.forEach((key, value) -> exchange.getMessage().setHeader(key, value));
        return exchange;
    }

    protected Map<String, Object> enrichParameterValues(String operationId) {
        Map<String, Object> params = new HashMap<>();

        OpenApiParameterExtractor extractor = new OpenApiParameterExtractor(connectionProperties.getEakteApiVersion());
        var readApentryParameters = extractor.getParameterNamesForOperation(operationId);

        readApentryParameters.forEach(param -> {
            Optional<String> paramValue = Optional.ofNullable(environment.getProperty(EFILE_CASE_FILE_PROPERTIES.concat(param.replace("-", ""))));
            paramValue.ifPresent(value -> params.put(param, value));
        });
        return params;
    }

    protected Exchange createExchangeForReadCollections() {
        return createExchange(OperationId.READ_COLLECTIONS.getDescriptor());
    }

}
