package de.muenchen.eh.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageMetaDataBuilder {

    private MsgIdentificationBuilder msgIdentification;
    private DeliveryAttributesBuilder deliveryAttributes;
    private OriginatorBuilder originators;
    private DestinationBuilder destinations;
    private QualifierBuilder qualifier;

    public MessageMetaData build() {

        MessageMetaData meta = new MessageMetaData();

        if (msgIdentification != null)
            meta.setMsgIdentification(msgIdentification.build());

        if (deliveryAttributes != null)
            meta.setDeliveryAttributes(deliveryAttributes.build());

        if (originators != null)
            meta.setOriginators(originators.build());

        if (destinations != null)
            meta.setDestinations(destinations.build());

        if (qualifier != null)
            meta.setQualifier(qualifier.buildJaxb());

        return meta;
    }
}

