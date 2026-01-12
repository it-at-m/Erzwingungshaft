package de.muenchen.eh;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class StopExchange implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.setRouteStop(true);
    }
}
