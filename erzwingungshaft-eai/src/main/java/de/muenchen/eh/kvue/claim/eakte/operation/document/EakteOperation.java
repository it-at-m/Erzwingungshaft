package de.muenchen.eh.kvue.claim.eakte.operation.document;

import org.apache.camel.Exchange;

public interface EakteOperation {

    public void execute(Exchange exchange);

}
