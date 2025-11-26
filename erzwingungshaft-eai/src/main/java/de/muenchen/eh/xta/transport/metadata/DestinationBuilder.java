package de.muenchen.eh.xta.transport.metadata;

import genv3.eu.osci.ws.x2014.x10.transport.DestinationsType;
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

