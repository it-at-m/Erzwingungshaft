package de.muenchen.eh;

import de.muenchen.eh.infrastructure.log.Constants;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.springframework.stereotype.Component;

@Component
public class GlobalHandlerRouteBuilder extends BaseRouteBuilder {

    @Override
    public void configure() {

        Predicate claimOrClaimImportExists = PredicateBuilder.or(exchangeProperty(Constants.CLAIM).isNotNull(),
                exchangeProperty(Constants.CLAIM_IMPORT).isNotNull());

        Predicate identifierImport = PredicateBuilder.or(exchangeProperty(Constants.IDENTIFIER_CREATOR).isNotNull());

        // spotless:off
        from(GLOBAL_EXCEPTION_HANDLER).routeId("global-exception-handler")
                .log(LoggingLevel.ERROR, "${exception}") // oder log more details
                .choice()
                .when(claimOrClaimImportExists)
                .bean("logServiceError", "logError")
                .when(identifierImport)
                .bean("logServiceIdentifier", "logError")
                .otherwise()
                .log(LoggingLevel.ERROR, "${exception.stacktrace}")
                .end()
                .process(new StopExchange());
        // spotless:on
    }

}
