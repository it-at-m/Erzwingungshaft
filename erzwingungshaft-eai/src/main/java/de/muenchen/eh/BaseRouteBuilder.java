package de.muenchen.eh;

import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
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

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                    .when(simple(String.format("${header.%1$s} != null || ${header.%2$s} != null", Constants.CLAIM, Constants.CLAIM_IMPORT)))
                     .bean("logServiceError", "logError")
                .end()
                .to("{{xjustiz.interface.file.error}}");;

        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                    .when(simple(String.format("${header.%s} != null", Constants.CLAIM)))
                        .bean("logServiceClaim", "logIllegalArgumentException")
                .end()
                .to("{{xjustiz.interface.file.error}}");

    }

}
