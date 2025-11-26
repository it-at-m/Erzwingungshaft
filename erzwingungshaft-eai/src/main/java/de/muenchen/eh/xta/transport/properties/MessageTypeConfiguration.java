package de.muenchen.eh.xta.transport.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Setter
public class MessageTypeConfiguration {

    private String listUri;
    private String listVersion;
    private String payloadSchema;
    private String code;
}
