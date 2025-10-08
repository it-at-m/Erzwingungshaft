package de.muenchen.eh.kvue.claim.efile.operation.document;

import org.apache.camel.Exchange;

abstract class EfileRecordTemplate {

    public final void execute(Exchange exchange) {
        findCollection(exchange);
        if (exchange.isRouteStop())
            return;
        checkCaseFiles(exchange);
        addCaseFile(exchange);
        addBussgeldverfahren(exchange);
        addDocuments(exchange);
    }

    protected abstract void findCollection(Exchange exchange);
    protected abstract void checkCaseFiles(Exchange exchange);
    protected abstract void addCaseFile(Exchange exchange);
    protected abstract void addBussgeldverfahren(Exchange exchange);
    protected abstract void addDocuments(Exchange exchange);

}
