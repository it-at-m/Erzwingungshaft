package de.muenchen.eh.infrastructure.integration.xta.transport.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Setter
public class PartyIdentifierConfiguration {

    private String destination;
    private String originator;
    private String type;
    private String name;

}
