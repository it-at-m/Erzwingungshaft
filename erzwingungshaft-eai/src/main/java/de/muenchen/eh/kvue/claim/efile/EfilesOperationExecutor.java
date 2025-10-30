package de.muenchen.eh.kvue.claim.efile;

import de.muenchen.eh.kvue.claim.efile.operation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class EfilesOperationExecutor implements Processor {

    private final FindCollection collectionFinder;
    private final AddFile addFile;
    private final AddFine addFine;
    private final AddOutgoing addOutgoing;

    @Override
    public void process(Exchange exchange) throws Exception {

        EfileRecord operation = new EfileRecord(collectionFinder, addFile, addFine, addOutgoing);
        operation.execute(exchange);

    }

}
