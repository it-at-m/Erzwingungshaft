package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.LogServiceClaim;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimXJustizXmlEnricher implements Processor {

    private final LogServiceClaim logServiceClaim;

    @Produce(ClaimRouteBuilder.PROCESS_XJUSTIZ_DOCUMENT)
    private ProducerTemplate xjustizProducer;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);

        Exchange xjustizContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(processingDataWrapper.getContentContainer()).build();
        Exchange xJustizXML = xjustizProducer.send(xjustizContent);
        processingDataWrapper.setXjustizXml(xJustizXML.getMessage().getBody(String.class));
        exchange.setException(xJustizXML.getException());

        logServiceClaim.logXml(exchange);

    }

}
