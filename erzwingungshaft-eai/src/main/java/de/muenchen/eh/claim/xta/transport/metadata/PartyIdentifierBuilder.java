package de.muenchen.eh.claim.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.PartyIdentifierType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyIdentifierBuilder {

    private String name;
    private String value;
    private String type;

    public PartyIdentifierType build() {
        PartyIdentifierType pit = new PartyIdentifierType();
        pit.setName(name);
        pit.setValue(value);
        pit.setType(type);
        return pit;
    }
}
