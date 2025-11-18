package de.muenchen.eh.xta;

import de.muenchen.eh.BaseRouteBuilder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class XtaRouteBuilder extends BaseRouteBuilder {

    public static final String BEPBO_MANAGEMENT_PORT = "direct:managementPort";

    @Override
    public void configure() {

        super.configure();

        from("direct:managementPort").routeId("xta-management-port")
                .to("cxf:bean:managementPort")
                .process(exchange -> {exchange.toString();})
                .log(LoggingLevel.DEBUG, "${body}");
    }
}
