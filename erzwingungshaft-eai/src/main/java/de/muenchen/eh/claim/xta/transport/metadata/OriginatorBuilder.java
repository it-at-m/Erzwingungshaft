package de.muenchen.eh.claim.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.OriginatorsType;
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
