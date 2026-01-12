package de.muenchen.eh.claim.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.MessageMetaData;
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
