package de.muenchen.eh.claim.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.DestinationsType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationBuilder {

    private PartyBuilder reader;

    public DestinationsType build() {
        DestinationsType dt = new DestinationsType();
        if (reader != null) {
            dt.setReader(reader.build());
        }
        return dt;
    }
}
