package de.muenchen.eh.kvue.claim.efile;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eakte.api.rest.model.CreateContentObjectAntwortDTO;
import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static de.muenchen.eh.kvue.claim.efile.operation.OperationId.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class EfileResponseMapper implements Processor {

    private final ObjectMapper mapper;

    @Override
    public void process(Exchange exchange) throws Exception {

        String operationId = exchange.getMessage().getHeader(Constants.OPERATION_ID, String.class);
        String json = exchange.getMessage().getBody(String.class);
        log.debug(json);

        if (operationId.equals(READ_COLLECTIONS.getDescriptor()))
            exchange.getMessage().setBody(mapper.readValue(json, ReadApentryAntwortDTO.class));
        else if (operationId.equals(CREATE_FILE.getDescriptor()) || operationId.equals(CREATE_FINE.getDescriptor()))
            exchange.getMessage().setBody(mapper.readValue(json, DmsObjektResponse.class));
        else if (operationId.equals(CREATE_OUTGOING.getDescriptor()))
            exchange.getMessage().setBody(mapper.readValue(json, CreateOutgoingAntwortDTO.class));
        else if (operationId.equals(UPDATE_SUBJECT_DATA.getDescriptor()))
            exchange.getMessage().setBody(mapper.readValue(json, Map.class));
        else if (operationId.equals(CREATE_CONTENT_OBJECT.getDescriptor()))
            exchange.getMessage().setBody(mapper.readValue(json, CreateContentObjectAntwortDTO.class));
        else
            exchange.setException(new IllegalArgumentException("Unknown openapi.operationId : ".concat(operationId)));

        log.debug(exchange.getMessage().getBody().toString());

    }
}

