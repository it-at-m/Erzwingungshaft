package de.muenchen.eh.infrastructure.integration.efile.operation.eapl;

import org.apache.camel.Exchange;

public abstract class AbstractEAPLExecute {

    protected abstract void execute(Exchange exchange);

}
