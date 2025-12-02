package de.muenchen.eh.claim.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.DeliveryAttributesType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryAttributesBuilder {

    private XMLGregorianCalendar origin;

    public DeliveryAttributesType build() {
        DeliveryAttributesType da = new DeliveryAttributesType();
        da.setOrigin(origin);
        return da;
    }
}
