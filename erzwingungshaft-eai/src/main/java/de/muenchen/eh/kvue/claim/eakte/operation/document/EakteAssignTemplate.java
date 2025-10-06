package de.muenchen.eh.kvue.claim.eakte.operation.document;

import org.apache.camel.Exchange;

abstract class EakteAssignTemplate {

    public final void execute(Exchange exchange) {
        findCollection(exchange);
        addEinzelakte(exchange);
        addBussgeldverfahren(exchange);
        addDocuments(exchange);
    }

    protected abstract void findCollection(Exchange exchange);
    protected abstract void addEinzelakte(Exchange exchange);
    protected abstract void addBussgeldverfahren(Exchange exchange);
    protected abstract void addDocuments(Exchange exchange);

}
