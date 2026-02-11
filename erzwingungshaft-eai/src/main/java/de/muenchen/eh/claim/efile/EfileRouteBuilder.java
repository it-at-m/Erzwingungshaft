package de.muenchen.eh.claim.efile;

import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.StopExchange;
import de.muenchen.eh.claim.efile.properties.ConnectionProperties;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EfileRouteBuilder extends BaseRouteBuilder {

    private final ConnectionProperties properties;

    public static final String MARSHAL_JSON_DMS_CONNECTION = "direct:marshal-json-eakte-connection";
    public static final String DMS_CONNECTION = "direct:eakte-connection";

    @Override
    public void configure() {

        super.configure();

        // spotless:off
        onException(HttpOperationFailedException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                    .when(exchangeProperty(Constants.CLAIM).isNotNull())
                        .bean("logServiceClaim", "logHttpOperationFailedException")
                .end()
                .process(new StopExchange());

        /*
         * To enable overwriting at this point, the 'servers' entry must be removed from the openapi.json.
         * ...
         * "servers": [
         * {
         * "url": ""
         * }
         * ],
         * ...
         */
        restConfiguration()
                .component("http")
                .host(properties.getHost())
                .scheme(properties.getScheme())
                .port(properties.getPort())
                .contextPath(properties.getContextPath());

        from(MARSHAL_JSON_DMS_CONNECTION).routeId("marshal-json-rest-openapi-eakte")
                .marshal().json(JsonLibrary.Jackson)
                .to(DMS_CONNECTION);

        from(DMS_CONNECTION).routeId("rest-openapi-eakte")
                .toD("rest-openapi:classpath:openapi/eakte-api-v1.2.4.json#${header.operationId}?componentName=http")
                .process("efileResponseMapper");

        // spotless:on

    }
}
