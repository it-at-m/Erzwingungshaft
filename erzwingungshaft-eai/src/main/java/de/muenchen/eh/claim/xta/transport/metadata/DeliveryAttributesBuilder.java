package de.muenchen.eh.claim.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.DeliveryAttributesType;
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
