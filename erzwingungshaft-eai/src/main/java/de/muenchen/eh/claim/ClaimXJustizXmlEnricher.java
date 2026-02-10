package de.muenchen.eh.claim;

import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.xjustiz.config.DynamicXmlMarshaller;
import de.muenchen.xjustiz.xjustiz0500straf.nachricht.ExternAnJustiz0500010DocumentStart;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimXJustizXmlEnricher implements Processor {

    @Value("${xjustiz.xjustiz0500straf.xsd.name}")
    private String schemaName;

    @Value("${xjustiz.xsd.path}")
    private String schemaPath;

    private final LogServiceClaim logServiceClaim;

    private final ExternAnJustiz0500010DocumentStart documentBuilder;

    @Produce(ClaimRouteBuilder.PROCESS_XJUSTIZ_DOCUMENT)
    private ProducerTemplate xjustizProducer;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimContentWrapper claimContentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

        Exchange xjustizContent = ExchangeBuilder.anExchange(exchange.getContext()).withHeader(DynamicXmlMarshaller.SCHEMA_PATH, schemaPath)
                .withHeader(DynamicXmlMarshaller.SCHEMA_NAME, schemaName).withBody(documentBuilder.start(claimContentWrapper.getContentContainer())).build();
        Exchange xJustizXML = xjustizProducer.send(xjustizContent);
        claimContentWrapper.setXjustizXml(xJustizXML.getMessage().getBody(String.class));
        exchange.setException(xJustizXML.getException());

        logServiceClaim.logXml(exchange);

    }

}
