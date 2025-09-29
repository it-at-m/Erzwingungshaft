package de.muenchen.eh.eakte;

import de.muenchen.eh.kvue.BaseRouteBuilder;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.Claim;
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
                .when(simple(String.format("${header.%s} != null", Constants.CLAIM)))
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

        // @TODO Remove static claim will be used in the meantime to develop the first serve of the OperationId call
        Claim testClaim = new Claim();
        testClaim.setId(1);

        from(DMS_CONNECTION).routeId("rest-openapi-eakte")
            .setHeader(Constants.CLAIM, constant(testClaim))
           .toD("rest-openapi:classpath:openapi/dmsresteai-openapi.json#${header.operationId}");

    }
}
