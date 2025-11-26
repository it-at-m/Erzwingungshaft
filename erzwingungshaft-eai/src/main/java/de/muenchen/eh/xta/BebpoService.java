package de.muenchen.eh.xta;

import de.muenchen.eh.xta.transport.XtaMessage;
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
