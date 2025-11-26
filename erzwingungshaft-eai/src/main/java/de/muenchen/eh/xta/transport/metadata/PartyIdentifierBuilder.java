package de.muenchen.eh.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.PartyIdentifierType;
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

