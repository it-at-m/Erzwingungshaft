package de.muenchen.eh.claim.xta;

import de.muenchen.eh.claim.xta.transport.metadata.PartyBuilder;
import de.muenchen.eh.claim.xta.transport.metadata.PartyIdentifierBuilder;
import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import de.muenchen.eh.db.entity.Xta;
import de.muenchen.eh.db.repository.XtaRepository;
import de.xoev.transport.xta._211.TransportReport;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XtaMessageStatusRefreshService implements Processor {

    private final CamelContext camelContext;
    private final XtaRepository xtaRepository;
    private final XtaClientConfiguration clientConfiguration;

    @Produce(XtaRouteBuilder.BEPBO_MANAGEMENT_PORT)
    private ProducerTemplate managementPort;

    @Override
    public void process(Exchange exchange) throws Exception {

        Xta xta = exchange.getMessage().getBody(Xta.class);

        // Transport report
        PartyIdentifierBuilder pt = PartyIdentifierBuilder.builder().name(clientConfiguration.getPartyIdentifier().getName())
                .type(clientConfiguration.getPartyIdentifier().getType())
                .value(clientConfiguration.getPartyIdentifier().getOriginator()).build();
        PartyBuilder.builder().identifier(pt).build();

        AttributedURIType attributedURIType = new AttributedURIType();
        attributedURIType.setValue(xta.getMessageId());

        Exchange requestTransportReport = ExchangeBuilder.anExchange(camelContext)
                .withBody(List.of(attributedURIType, PartyBuilder.builder().identifier(pt).build().build()))
                .withHeader(CxfConstants.OPERATION_NAME, "getTransportReport")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange responseTransportReport = managementPort.send(requestTransportReport);

        TransportReport transportReport = responseTransportReport.getMessage().getBody(TransportReport.class);
        xta.setTransportMessageStatus(transportReport.getMessageStatus().getStatus().intValueExact());
        xta.setUpdatedAt(Instant.now());

        xtaRepository.save(xta);

        if (responseTransportReport.isRouteStop()) {
            exchange.setRouteStop(true);
        }
    }
}
