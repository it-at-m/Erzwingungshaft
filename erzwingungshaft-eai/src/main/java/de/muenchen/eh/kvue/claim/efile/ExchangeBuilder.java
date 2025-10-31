package de.muenchen.eh.kvue.claim.efile;

import org.apache.camel.Exchange;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Fluent builder for configuring Apache Camel Exchange objects with
 * common headers and authentication.
 * <p>
 * Usage example:
 * <pre>
 * Exchange configuredExchange = ExchangeBuilder.create(exchange, "op-123")
 *     .withBasicAuth("user", "pass")
 *     .withRequestValidation(true)
 *     .build();
 * </pre>
 */
public class ExchangeBuilder {

    private final Exchange exchange;

    private ExchangeBuilder(Exchange exchange, String operationId) {
        this.exchange = exchange;
        this.exchange.getMessage().setHeader("operationId", operationId);
    }

    /**
     * Creates a new ExchangeBuilder with the specified operation ID.
     *
     * @param exchange    the Camel Exchange to configure
     * @param operationId the operation identifier to set as a header
     * @return a new ExchangeBuilder instance
     */
    public static ExchangeBuilder create(Exchange exchange, String operationId) {
        return new ExchangeBuilder(exchange, operationId);
    }

    /**
     * Configures HTTP Basic Authentication by setting the Authorization header.
     *
     * @param username the username for basic auth
     * @param password the password for basic auth
     * @return this builder for method chaining
     */
    public ExchangeBuilder withBasicAuth(String username, String password) {
        String auth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        exchange.getMessage().setHeader("Authorization", auth);
        return this;
    }

    /**
     * Enables or disables request validation.
     *
     * @param enabled true to enable request validation, false otherwise
     * @return this builder for method chaining
     */
    public ExchangeBuilder withRequestValidation(boolean enabled) {
        exchange.getMessage().setHeader("requestValidationEnabled", enabled);
        return this;
    }

    /**
     * Returns the configured Exchange.
     *
     * @return the configured Camel Exchange
     */
    public Exchange build() {
        return exchange;
    }
}

