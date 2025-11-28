package de.muenchen.eh.claim.xta.transport.metadata;

import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XtaMessageMetaData {

    private final XtaClientConfiguration clientConfiguration;

    private AttributedURIType attributedURIType;

    public MessageMetaData build(AttributedURIType attributedURIType) {

        this.attributedURIType = attributedURIType;

        return MessageMetaDataBuilder.builder()
                .msgIdentification(messageIdentification())
                .deliveryAttributes(getDeliveryAttributes())
                .originators(originator())
                .destinations(destination())
                .qualifier(getQualifier())
                .build()
                .build();
    }

    private DeliveryAttributesBuilder getDeliveryAttributes() {
        return DeliveryAttributesBuilder.builder()
                .origin(DatatypeFactory.newDefaultInstance()
                        .newXMLGregorianCalendar(new GregorianCalendar()))
                .build();
    }

    private QualifierBuilder getQualifier() {
        return QualifierBuilder.builder()
                .subject("ehaft-test-attachment")
                .service("")
                .messageType(
                        QualifierBuilder.MessageTypeBuilder.builder()
                                .listURI(clientConfiguration.getMessageType().getListUri())
                                .code(clientConfiguration.getMessageType().getCode())
                                .listVersionID(clientConfiguration.getMessageType().getListVersion())
                                .payloadSchema(clientConfiguration.getMessageType().getPayloadSchema())
                                .build())
                .build();
    }

    private DestinationBuilder destination() {
        return DestinationBuilder.builder()
                .reader(
                        PartyBuilder.builder()
                                .identifier(
                                        PartyIdentifierBuilder.builder()
                                                .value(clientConfiguration.getPartyIdentifier().getDestination())
                                                .build())
                                .build())
                .build();
    }

    private OriginatorBuilder originator() {
        return OriginatorBuilder.builder()
                .author(
                        PartyBuilder.builder()
                                .identifier(
                                        PartyIdentifierBuilder.builder()
                                                .name(clientConfiguration.getPartyIdentifier().getName())
                                                .value(clientConfiguration.getPartyIdentifier().getOriginator())
                                                .type(clientConfiguration.getPartyIdentifier().getType())
                                                .build())
                                .build())
                .build();
    }

    private MsgIdentificationBuilder messageIdentification() {
        return MsgIdentificationBuilder.builder()
                .messageID(attributedURIType)
                .build();
    }

}
