package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.EhServiceClaim;
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

    private final EhServiceClaim ehServiceClaim;

    @Produce(ClaimRouteBuilder.PROCESS_XJUSTIZ_DOCUMENT)
    private ProducerTemplate xjustizProducer;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimDataWrapper wrapper = exchange.getMessage().getBody(ClaimDataWrapper.class);

        Exchange xjustizContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(wrapper.getContentContainer()).build();
        Exchange xJustizXML = xjustizProducer.send(xjustizContent);
        wrapper.setXjustizXml(xJustizXML.getMessage().getBody(String.class));

        ehServiceClaim.logXml(exchange);

    }

}
