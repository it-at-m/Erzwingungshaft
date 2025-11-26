package de.muenchen.eh.claim.efile;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EfileResponseMapper implements Processor {

    private final ObjectMapper mapper;

    @Override
    public void process(Exchange exchange) throws Exception {

        String operationId = exchange.getMessage().getHeader(Constants.OPERATION_ID, String.class);
        String json = exchange.getMessage().getBody(String.class);

        if (operationId == null) {
            exchange.setException(new IllegalArgumentException("Missing header: EH_OPERATION_ID"));
            return;
        }
        if (json == null || json.isBlank()) {
            exchange.setException(new IllegalArgumentException("Empty response body for objectMapper operationId: " + operationId));
            return;
        }

        log.debug(json);

        OperationId opId = OperationId.fromDescriptor(operationId);
        exchange.getMessage().setBody(opId.parseResponse(json, mapper));

        log.debug(exchange.getMessage().getBody().toString());

    }
}
