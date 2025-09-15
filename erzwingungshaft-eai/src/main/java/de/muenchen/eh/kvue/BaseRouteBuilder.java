package de.muenchen.eh.kvue;

import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
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
                    .when(simple(String.format("${header.%1$s} != null || ${header.%2$s} != null", Constants.ENTRY_ENTITY, Constants.IMPORT_ENTITY)))
                     .bean("ehServiceError", "logError")
                .end()
                .to("{{xjustiz.interface.file.error}}");;

        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                    .when(simple(String.format("${header.%s} != null", Constants.ENTRY_ENTITY)))
                        .bean("ehServiceClaim", "logIllegalArgumentException")
                .end()
                .to("{{xjustiz.interface.file.error}}");

    }

}
