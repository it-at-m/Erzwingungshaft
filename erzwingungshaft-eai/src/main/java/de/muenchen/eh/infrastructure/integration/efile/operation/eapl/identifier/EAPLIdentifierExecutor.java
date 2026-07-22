package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.identifier;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("eAPLIdentifierExecutor")
@RequiredArgsConstructor
public class EAPLIdentifierExecutor implements Processor {

    private final EAPLIdentifierFine eaplIdentifierFine;

    @Override
    public void process(Exchange exchange) throws Exception {

        eaplIdentifierFine.execute(exchange);

    }
}
