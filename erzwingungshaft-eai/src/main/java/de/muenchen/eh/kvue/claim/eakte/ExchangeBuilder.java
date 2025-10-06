package de.muenchen.eh.kvue.claim.eakte;

import org.apache.camel.Exchange;
import java.util.Base64;

public class ExchangeBuilder {

    private final Exchange exchange;

    private ExchangeBuilder(Exchange exchange, String operationId) {
        this.exchange = exchange;
        this.exchange.getMessage().setHeader("operationId", operationId);
    }

    public static ExchangeBuilder create(Exchange exchange, String operationId) {
        return new ExchangeBuilder(exchange, operationId);
    }

    public ExchangeBuilder withBasicAuth(String username, String password) {
        String auth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        exchange.getMessage().setHeader("Authorization", auth);
        return this;
    }

    public ExchangeBuilder withRequestValidation(boolean enabled) {
        exchange.getMessage().setHeader("requestValidationEnabled", enabled);
        return this;
    }

    public Exchange build() {
        return exchange;
    }
}

