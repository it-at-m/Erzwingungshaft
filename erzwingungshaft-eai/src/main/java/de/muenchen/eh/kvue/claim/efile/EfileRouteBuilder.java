package de.muenchen.eh.kvue.claim.efile;

import de.muenchen.eakte.api.rest.model.CreateContentObjectAntwortDTO;
import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.StopExchange;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.properties.FileProperties;
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
    private final FileProperties fileProperties;

    public static final String MARSHAL_JSON_DMS_CONNECTION = "direct:marshal-json-eakte-connection";
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
                .component("http")
                .host(properties.getHost())
                .scheme(properties.getScheme())
                .port(properties.getPort())
                .contextPath(properties.getContextPath());

        from(MARSHAL_JSON_DMS_CONNECTION).routeId("marshal-json-rest-openapi-eakte")
                .marshal().json(JsonLibrary.Jackson)
                .to(DMS_CONNECTION);

        from(DMS_CONNECTION).routeId("rest-openapi-eakte")
                .log(LoggingLevel.DEBUG, "${body}")
                .toD("rest-openapi:classpath:openapi/eakte-api-v1.2.4.json#${header.operationId}?componentName=http")
                .choice()
                    .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.READ_COLLECTIONS.getDescriptor()))
                        .unmarshal().json(JsonLibrary.Jackson, ReadApentryAntwortDTO.class)
                        .log(LoggingLevel.INFO, "${header.objaddress} found with objektreferences count : ${body.getGiobjecttype().size()}")
                     .when(or(header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_FILE.getDescriptor()), header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_FINE.getDescriptor())))
                        .unmarshal().json(JsonLibrary.Jackson, DmsObjektResponse.class)
                        .log(LoggingLevel.DEBUG, "${body.objid} created.")
                    .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_OUTGOING.getDescriptor()))
                        .unmarshal().json(JsonLibrary.Jackson, CreateOutgoingAntwortDTO.class)
                        .log(LoggingLevel.DEBUG, "${body.objid} created.")
                    .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.UPDATE_SUBJECT_DATA.getDescriptor()))
                        .unmarshal().json(JsonLibrary.Jackson)
                        .log(LoggingLevel.DEBUG, "${body} created.")
                    .when(header(Constants.OPERATION_ID).isEqualTo(OperationId.CREATE_CONTENT_OBJECT.getDescriptor()))
                        .unmarshal().json(JsonLibrary.Jackson, CreateContentObjectAntwortDTO.class)
                        .log(LoggingLevel.DEBUG, "${body.objid} created.")
                    .otherwise()
                        .process(exchange -> {
                            exchange.setException(new IllegalArgumentException("Unknown openapi.operationId : ".concat((String) exchange.getMessage().getHeader(Constants.OPERATION_ID))));
                        })
                .end();

    }
}
