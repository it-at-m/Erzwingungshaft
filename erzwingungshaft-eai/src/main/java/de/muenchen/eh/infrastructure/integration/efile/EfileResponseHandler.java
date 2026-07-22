package de.muenchen.eh.infrastructure.integration.efile;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.log.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EfileResponseHandler implements Processor {

    private final ObjectMapper mapper;

    @Override
    public void process(Exchange exchange) throws Exception {

        String operationId = exchange.getMessage().getHeader(Constants.OPERATION_ID, String.class);
        String json = exchange.getMessage().getBody(String.class);

        if (operationId == null) {
            exchange.setException(new IllegalArgumentException("Missing header: EH_OPERATION_ID"));
            return;
        }

        if (operationId.equals(OperationId.UPDATE_USER_FORMS_DATA.getDescriptor()))
            return;

        if (json == null || json.isBlank()) {
            exchange.setException(new IllegalArgumentException("Empty response body for objectMapper operationId: " + operationId));
            return;
        }

        // Logging
        if (log.isDebugEnabled()) {
            exchange.getMessage().getHeaders().forEach((k, v) -> log.debug("header[{}] = {}", k, v));
            log.debug(json);
        }

        // Unmarshall
        OperationId opId = OperationId.fromDescriptor(operationId);
        exchange.getMessage().setBody(opId.parseResponse(json, mapper));

    }
}
