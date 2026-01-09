package de.muenchen.eh.claim.efile;

import de.muenchen.eh.claim.efile.operation.EfileRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class EfilesOperationExecutor implements Processor {

    private final EfileRecord operation;

    @Override
    public void process(Exchange exchange) throws Exception {

        operation.execute(exchange);

    }
}
