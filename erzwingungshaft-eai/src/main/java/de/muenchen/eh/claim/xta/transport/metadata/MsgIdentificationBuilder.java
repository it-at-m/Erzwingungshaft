package de.muenchen.eh.claim.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.MsgIdentificationType;
import lombok.Builder;
import lombok.Getter;
import org.apache.cxf.ws.addressing.AttributedURIType;

@Getter
@Builder
public class MsgIdentificationBuilder {

    private AttributedURIType messageID;

    public MsgIdentificationType build() {
        MsgIdentificationType msg = new MsgIdentificationType();
        msg.setMessageID(messageID);
        return msg;
    }
}
