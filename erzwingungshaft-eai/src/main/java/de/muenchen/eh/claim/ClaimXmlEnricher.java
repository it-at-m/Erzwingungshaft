package de.muenchen.eh.claim;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimXmlEnricher implements Processor {

    @Produce(value = ClaimRouteBuilder.PROCESS_XJUSTIZ_DOCUMENT)
    private ProducerTemplate xmlProducer;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimContentWrapper dataContentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        Exchange xmlRequest = ExchangeBuilder.anExchange(exchange.getContext()).withBody(dataContentWrapper.getContentContainer()).build();
        Exchange xmlResponse = xmlProducer.send(xmlRequest);
        dataContentWrapper.setXjustizXml(xmlResponse.getMessage().getBody(String.class));

    }
}
