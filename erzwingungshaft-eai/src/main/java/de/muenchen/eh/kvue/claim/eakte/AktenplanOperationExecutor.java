package de.muenchen.eh.kvue.claim.eakte;

import de.muenchen.eh.kvue.claim.eakte.operation.document.EakteAssign;
import de.muenchen.eh.kvue.claim.eakte.operation.document.FindCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class AktenplanOperationExecutor implements Processor {

    private final FindCollection collectionFinder;

    @Override
    public void process(Exchange exchange) throws Exception {

        EakteAssign operation = new EakteAssign(collectionFinder);
        operation.execute(exchange);

    }
}
