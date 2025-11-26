package de.muenchen.eh.claim.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.PartyType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyBuilder {

    private PartyIdentifierBuilder identifier;

    public PartyType build() {
        PartyType pt = new PartyType();
        if (identifier != null) {
            pt.setIdentifier(identifier.build());
        }
        return pt;
    }
}

