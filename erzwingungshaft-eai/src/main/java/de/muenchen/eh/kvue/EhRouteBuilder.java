package de.muenchen.eh.kvue;

import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EhRouteBuilder extends RouteBuilder {

    @Value("${xjustiz.interface.file.line-break}")
    private String lineBreak;

    public static final String DIRECT_ROUTE = "direct:eai-route";

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .bean("ehService", "logError")
                .to("{{xjustiz.interface.file.error}}");;

        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .bean("ehService", "logIllegalArgumentException")
                .to("{{xjustiz.interface.file.error}}");

        from("{{xjustiz.interface.file.consume")
                .routeId("kvue-eh-processing")
                .split(body().tokenize(lineBreak))
                .to("log:de.muenchen.eh?level=DEBUG")
                .bean("ehService", "logEntry")
                .unmarshal().bindy(BindyType.Fixed, EhCase.class)
                .to("log:de.muenchen.eh?level=DEBUG")
                .bean("ehService", "logUnmarshall")
                .transform().simple("${body.supplyXJustizRequestContent()}")
                .bean("ehService", "logContent")
                .to("{{xjustiz.interface.document.processor}}")
                .to("log:de.muenchen.eh?level=DEBUG")
                .bean("ehService", "logXml")
                .to("{{xjustiz.interface.eakte}}");

    }

}
