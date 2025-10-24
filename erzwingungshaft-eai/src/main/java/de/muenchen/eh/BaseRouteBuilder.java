package de.muenchen.eh;

import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseRouteBuilder extends RouteBuilder {

    @Value("${xjustiz.interface.file.line-break}")
    protected String lineBreak;

    @Override
    public void configure() {

        Predicate claimOrClaimImportExists = PredicateBuilder.or(exchangeProperty(Constants.CLAIM).isNotNull(), exchangeProperty(Constants.CLAIM_IMPORT).isNotNull());

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                .when(claimOrClaimImportExists)
                     .bean("logServiceError", "logError")
                .end()
                .process(new StopExchange());

        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                    .when(exchangeProperty(Constants.CLAIM).isNotNull())
                        .bean("logServiceClaim", "logIllegalArgumentException")
                .end()
                .process(new StopExchange());

    }

}
