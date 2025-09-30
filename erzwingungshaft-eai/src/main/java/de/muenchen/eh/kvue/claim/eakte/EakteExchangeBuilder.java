package de.muenchen.eh.kvue.claim.eakte;

import org.apache.camel.Exchange;
import java.util.Base64;

public class EakteExchangeBuilder {

    private final Exchange exchange;

    private EakteExchangeBuilder(Exchange exchange, String operationId) {
        this.exchange = exchange;
        this.exchange.getMessage().setHeader("operationId", operationId);
    }

    public static EakteExchangeBuilder create(Exchange exchange, String operationId) {
        return new EakteExchangeBuilder(exchange, operationId);
    }

    public EakteExchangeBuilder withBasicAuth(String username, String password) {
        String auth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        exchange.getMessage().setHeader("Authorization", auth);
        return this;
    }

    public EakteExchangeBuilder withRequestValidation(boolean enabled) {
        exchange.getMessage().setHeader("requestValidationEnabled", enabled);
        return this;
    }

    public Exchange build() {
        return exchange;
    }
}

