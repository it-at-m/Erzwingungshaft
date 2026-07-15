package de.muenchen.eh.infrastructure.integration.efile.operation.eapl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("eAPLExecutor")
@RequiredArgsConstructor
@Log4j2
public class EAPLExecutor implements Processor {

    private final EAPL eapl;

    @Override
    public void process(Exchange exchange) throws Exception {

        eapl.execute(exchange);

    }
}




