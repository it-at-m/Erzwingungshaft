package de.muenchen.eh.kvue.claim.eakte;

import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.kvue.claim.eakte.operation.OperationId;
import de.muenchen.eh.kvue.claim.eakte.properties.AktenplanEinzelaktenProperties;
import de.muenchen.eh.kvue.claim.eakte.properties.ConnectionProperties;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EakteRouteBuilder extends BaseRouteBuilder {

    private final ConnectionProperties properties;
    private final AktenplanEinzelaktenProperties einzelaktenProperties;

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
                .end();

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
                .component("rest-openapi")
                .host(properties.getHost())
                .scheme(properties.getScheme())
                .port(properties.getPort())
                .contextPath(properties.getContextPath());

      from(DMS_CONNECTION).routeId("rest-openapi-eakte")
                .toD("rest-openapi:classpath:openapi/dmsresteai-openapi.json#${header.operationId}")
                .choice()
                   .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.READ_APENTRY))
                    .unmarshal().json(JsonLibrary.Jackson, ReadApentryAntwortDTO.class)
                    .log(LoggingLevel.INFO, einzelaktenProperties.getAktenplanEintrag().concat("(").concat(einzelaktenProperties.getJoboe()).concat(") found with objektreferences count : ${body.getGiobjecttype().size()}"))
                  .otherwise()
                     .throwException(new IllegalArgumentException("Unkown message type."))
                .end();


    }
}
