package de.muenchen.eh.claim.xta;

import de.muenchen.eh.BaseRouteBuilder;
import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;

@Component
public class XtaRouteBuilder extends BaseRouteBuilder {

    public static final String BEPBO_MANAGEMENT_PORT = "direct:managementPort";
    public static final String BEPBO_SEND_PORT = "direct:sendPort";

    @Override
    public void configure() {

        super.configure();

        from(BEPBO_MANAGEMENT_PORT).routeId("xta-management-port")
                .to("cxf:bean:managementPort").id("management-port")
                .log(LoggingLevel.DEBUG, "${body}");

        from(BEPBO_SEND_PORT).routeId("xta-send-port")
                .to("cxf:bean:sendPort").id("send-port")
                .log(LoggingLevel.DEBUG, "${body}");
    }
}
