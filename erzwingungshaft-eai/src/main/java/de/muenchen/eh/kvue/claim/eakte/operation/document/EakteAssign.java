package de.muenchen.eh.kvue.claim.eakte.operation.document;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;

@RequiredArgsConstructor
public class EakteAssign extends EakteAssignTemplate {

   private final FindCollection collectionFinder;

    protected void findCollection(Exchange exchange) {
        collectionFinder.execute(exchange);
    }

    protected void addEinzelakte(Exchange exchange) {

    }

    protected void addBussgeldverfahren(Exchange exchange) {

    }

    protected void addDocuments(Exchange exchange) {

    }
}
