package de.muenchen.eh.claim.efile.operation;

import org.apache.camel.Exchange;

abstract class EfileRecordTemplate {

    public final void execute(Exchange exchange) {

        findCollection(exchange);
        if (exchange.isRouteStop())
            return;

        addFile(exchange);
        if (exchange.isRouteStop())
            return;

        addFine(exchange);
        if (exchange.isRouteStop())
            return;

        addOutgoing(exchange);

    }

    protected abstract void findCollection(Exchange exchange);

    protected abstract void addFile(Exchange exchange);

    protected abstract void addFine(Exchange exchange);

    protected abstract void addOutgoing(Exchange exchange);

}
