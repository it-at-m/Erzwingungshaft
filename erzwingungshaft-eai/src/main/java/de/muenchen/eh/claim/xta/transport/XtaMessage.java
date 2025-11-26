package de.muenchen.eh.claim.xta.transport;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.entity.Xta;
import de.muenchen.eh.log.db.repository.XtaRepository;
import de.muenchen.eh.claim.xta.XtaRouteBuilder;
import de.muenchen.eh.claim.xta.transport.container.RequestGenericContentContainerBuilder;
import de.muenchen.eh.claim.xta.transport.metadata.PartyBuilder;
import de.muenchen.eh.claim.xta.transport.metadata.PartyIdentifierBuilder;
import de.muenchen.eh.claim.xta.transport.metadata.RequestMessageMetaDataBuilder;
import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import genv3.de.xoev.transport.xta.x211.TransportReport;
import genv3.eu.osci.ws.x2008.x05.transport.X509TokenContainerType;
import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XtaMessage {

    private final CamelContext camelContext;
    private final RequestGenericContentContainerBuilder requestGenericContentContainerBuilder;
    private final RequestMessageMetaDataBuilder requestMessageMetaDataBuilder;
    private final XtaRepository xtaRepository;
    private final LogServiceClaim logServiceClaim;
    private final XtaClientConfiguration clientConfiguration;

    @Produce(XtaRouteBuilder.BEPBO_SEND_PORT)
    private ProducerTemplate sendPort;

    @Produce(XtaRouteBuilder.BEPBO_MANAGEMENT_PORT)
    private ProducerTemplate managementPort;

    public Exchange send(Exchange exchange) {

        // Message id
        Exchange requestMessageId = ExchangeBuilder.anExchange(camelContext)
                .withBody(Collections.emptyList())
                .withHeader(CxfConstants.OPERATION_NAME, "createMessageId")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange responseMessageId = managementPort.send(requestMessageId);

        if (responseMessageId.isRouteStop()) {
            exchange.setRouteStop(true);
            return exchange;
        }

        AttributedURIType attributedURIType = responseMessageId.getIn().getBody(AttributedURIType.class);
        ClaimContentWrapper contentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        var importClaim = contentWrapper.getClaimImport();

        Xta xta = new Xta();
        xta.setClaimImportId(importClaim.getId());
        xta.setMessageId(attributedURIType.getValue());

        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.XTA_MESSAGE_ID, MessageType.INFO, exchange);

        // Send message
        GenericContentContainer contentContainer = requestGenericContentContainerBuilder.build(contentWrapper);
        MessageMetaData messageMetaData = requestMessageMetaDataBuilder.build(attributedURIType);

        Exchange requestSend = ExchangeBuilder.anExchange(camelContext)
                .withBody(List.of(contentContainer, messageMetaData, new X509TokenContainerType()))
                .withHeader("MessageID", attributedURIType.getValue())
                .build();

        Exchange responseSend = sendPort.send(requestSend);

        xta.setSendHttpResponseCode(responseSend.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class));

        if (responseSend.isRouteStop()) {
            exchange.setRouteStop(true);
            return exchange;
        }

        // Transport report
        PartyIdentifierBuilder pt = PartyIdentifierBuilder.builder().name(clientConfiguration.getPartyIdentifier().getName())
                .type(clientConfiguration.getPartyIdentifier().getType())
                .value(clientConfiguration.getPartyIdentifier().getOriginator()).build();
        PartyBuilder.builder().identifier(pt).build();

        Exchange requestTransportReport = ExchangeBuilder.anExchange(camelContext)
                .withBody(List.of(attributedURIType, PartyBuilder.builder().identifier(pt).build().build()))
                .withHeader(CxfConstants.OPERATION_NAME, "getTransportReport")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange responseTransportReport = managementPort.send(requestTransportReport);

        TransportReport transportReport = responseTransportReport.getMessage().getBody(TransportReport.class);
        xta.setTransportMessageStatus(transportReport.getMessageStatus().getStatus().intValueExact());

        xtaRepository.save(xta);

        if (responseTransportReport.isRouteStop()) {
            exchange.setRouteStop(true);
        }

        return exchange;

    }
}
