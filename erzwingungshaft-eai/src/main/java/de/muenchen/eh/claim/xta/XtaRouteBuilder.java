package de.muenchen.eh.claim.xta;

import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.db.service.XtaService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XtaRouteBuilder extends BaseRouteBuilder {

    public static final String BEPBO_MANAGEMENT_PORT = "direct:managementPort";
    public static final String BEPBO_SEND_PORT = "direct:sendPort";
    public static final String BEPBO_REFRESH_MESSAGE_STATUS = "direct:transportMessageStatus";

    private final XtaService xtaService;

    @Override
    public void configure() {

        super.configure();

        from(BEPBO_MANAGEMENT_PORT).routeId("xta-management-port")
                .to("cxf:bean:managementPort").id("management-port")
                .log(LoggingLevel.DEBUG, "${body}");

        from(BEPBO_SEND_PORT).routeId("xta-send-port")
                .to("cxf:bean:sendPort").id("send-port")
                .log(LoggingLevel.DEBUG, "${body}");

        from(BEPBO_REFRESH_MESSAGE_STATUS).routeId("transport-messsage-status")
                .setBody(method(xtaService, "refreshMessageStatus"))
                .split().body()
                .process("xtaMessageStatusRefreshService");

    }
}
