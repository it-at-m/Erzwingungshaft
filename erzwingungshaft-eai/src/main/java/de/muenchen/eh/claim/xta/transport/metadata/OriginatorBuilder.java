package de.muenchen.eh.claim.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.OriginatorsType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OriginatorBuilder {

    private PartyBuilder author;

    public OriginatorsType build() {
        OriginatorsType ot = new OriginatorsType();
        if (author != null) {
            ot.setAuthor(author.build());
        }
        return ot;
    }
}

