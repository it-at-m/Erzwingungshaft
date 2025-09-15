package de.muenchen.eh.kvue.cases;

import de.muenchen.eh.common.ExtractEhIdentifier;
import de.muenchen.eh.log.Constants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class EhIdentifier implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Constants.EH_IDENTIFIER, ExtractEhIdentifier.getIdentifier(exchange.getIn().getHeader(Exchange.FILE_NAME_CONSUMED, String.class)));
    }
}
