package de.muenchen.eh.kvue.claim.efile.operation.document;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;

@RequiredArgsConstructor
public class EfileRecord extends EfileRecordTemplate {

    private final FindCollection collectionFinder;
    private final AddCaseFile addCaseFile;
    private final AddFine addFine;
    private final AddOutgoing addOutgoing;

    @Override
    protected void findCollection(Exchange exchange) {
        collectionFinder.execute(exchange);
    }

    @Override
    protected void addCaseFile(Exchange exchange) {
        addCaseFile.execute(exchange);
    }

    @Override
    protected void addFine(Exchange exchange) {
        addFine.execute(exchange);
    }

    @Override
    protected void addOutgoing(Exchange exchange) {
        addOutgoing.execute(exchange);
    }
}
