package de.muenchen.eh.infrastructure.integration.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.PartyType;
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



