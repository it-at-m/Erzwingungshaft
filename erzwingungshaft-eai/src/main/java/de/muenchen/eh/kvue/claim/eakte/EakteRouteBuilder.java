package de.muenchen.eh.kvue.claim.eakte;

import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.log.Constants;
import org.apache.camel.LoggingLevel;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EakteRouteBuilder extends BaseRouteBuilder {

    @Autowired
    private EakteConnectionProperties properties;

    public static final String DMS_CONNECTION = "direct:eakte-connection";

    @Override
    public void configure() {

        super.configure();

        onException(HttpOperationFailedException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                        .when(exchangeProperty(Constants.CLAIM).isNotNull())
                            .bean("logServiceClaim", "logHttpOperationFailedException")
                .end()
                .to("{{xjustiz.interface.file.error}}");

        /*
             To enable overwriting at this point, the 'servers' entry must be removed from the openapi.json.
             ...
            "servers": [
                {
                    "url": ""
                }
            ],
            ...
         */
        restConfiguration()
                .component("http")
                .host(properties.getHost())
                .scheme(properties.getScheme())
                .port(properties.getPort())
                .contextPath(properties.getContextPath());


        from(DMS_CONNECTION).routeId("rest-openapi-eakte")
                .toD("rest-openapi:classpath:openapi/dmsresteai-openapi.json#${header.operationId}")
                .convertBodyTo(String.class);

    }
}
