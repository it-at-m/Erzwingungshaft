package de.muenchen.eh.kvue.claim.efile;

import de.muenchen.eakte.api.rest.model.CreateContentObjectAntwortDTO;
import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.StopExchange;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.properties.AuthentificationProperties;
import de.muenchen.eh.kvue.claim.efile.properties.ConnectionProperties;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import static org.apache.camel.support.builder.PredicateBuilder.or;

@Component
@RequiredArgsConstructor
public class EfileRouteBuilder extends BaseRouteBuilder {

    private final ConnectionProperties properties;
    private final AuthentificationProperties authentificationProperties;

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
                .process(new StopExchange());

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
                .marshal().json(JsonLibrary.Jackson)
                .toD("rest-openapi:classpath:openapi/dmsresteai-openapi.json#${header.operationId}")
                .choice()
                   .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.READ_CASE_FILE_COLLECTIONS))
                       .unmarshal().json(JsonLibrary.Jackson, ReadApentryAntwortDTO.class)
                       .log(LoggingLevel.INFO, "${header.objaddress} found with objektreferences count : ${body.getGiobjecttype().size()}")
                   .when(or(header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_FILE), header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_FINE) ))
                      .unmarshal().json(JsonLibrary.Jackson, DmsObjektResponse.class)
                      .log(LoggingLevel.DEBUG, "${body.objid} created.")
                  .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_OUTGOING))
                      .unmarshal().json(JsonLibrary.Jackson, CreateOutgoingAntwortDTO.class)
                      .log(LoggingLevel.DEBUG, "${body.objid} created.")
                  .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_CONTENT_OBJECT))
                      .unmarshal().json(JsonLibrary.Jackson, CreateContentObjectAntwortDTO.class)
                      .log(LoggingLevel.DEBUG, "${body.objid} created.")
                  .otherwise()
                     .throwException(new IllegalArgumentException("Unkown message type."))
                .end();


    }
}
