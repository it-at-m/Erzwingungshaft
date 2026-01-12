package de.muenchen.eh.claim.xta;

import de.muenchen.eh.claim.xta.transport.XtaMessage;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BebpoService implements Processor {

    private final XtaMessage message;

    @Override
    public void process(Exchange exchange) throws Exception {

        message.send(exchange);

    }
}
