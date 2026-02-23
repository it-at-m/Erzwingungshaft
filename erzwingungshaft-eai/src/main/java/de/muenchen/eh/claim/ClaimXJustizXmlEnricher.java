package de.muenchen.eh.claim;

import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.xjustiz.config.DynamicXmlMarshaller;
import de.muenchen.xjustiz.xjustiz0500straf.nachricht.ExternAnJustiz0500010DocumentStart;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ClaimXJustizXmlEnricher implements Processor {

    private final XJustizProperties xjustizProperties;

    private final LogServiceClaim logServiceClaim;

    private final ExternAnJustiz0500010DocumentStart documentBuilder;

    @Produce(ClaimRouteBuilder.PROCESS_XJUSTIZ_DOCUMENT)
    private ProducerTemplate xjustizProducer;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimContentWrapper claimContentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

        Exchange xjustizContent = ExchangeBuilder.anExchange(exchange.getContext())
                .withHeader(DynamicXmlMarshaller.SCHEMA_PATH, xjustizProperties.getXsd().getPath())
                .withHeader(DynamicXmlMarshaller.SCHEMA_NAME, xjustizProperties.getXjustiz0500straf().getXsd().getName())
                .withBody(documentBuilder.start(claimContentWrapper.getContentContainer())).build();
        Exchange xJustizXML = xjustizProducer.send(xjustizContent);
        claimContentWrapper.setXjustizXml(xJustizXML.getMessage().getBody(String.class));
        exchange.setException(xJustizXML.getException());

        logServiceClaim.logXml(exchange);

    }

}
