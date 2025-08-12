package de.muenchen.erzwingungshaft;

import de.muenchen.erzwingungshaft.xta.XtaSendService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EaiRouteBuilder extends RouteBuilder {

    @Value("${output}")
    private String outputRoute;

    private static final String XTA2_BEBPO_ENDPOINT = "xta2-bebpo-endpoint";

    public static final String DIRECT_ROUTE = "direct:eai-route";

    private static final String FILE_INPUT_PATH = "src/main/resources/test/xta2";
    private static final String FILE_ROUTE_ID = "file-reading-route";

    @Override
    public void configure() {
        onException(Exception.class).handled(true).log(LoggingLevel.ERROR, "${exception}");

        final var fileEndpoint = StaticEndpointBuilders.file(FILE_INPUT_PATH).noop(true);

        from(fileEndpoint)
                .routeId(FILE_ROUTE_ID)
                .validate(body().isNotNull())
                .log(LoggingLevel.DEBUG, "${body}")
                .bean(XtaSendService.XTA_SEND_SERVICE_BEAN_NAME);
    }
}
