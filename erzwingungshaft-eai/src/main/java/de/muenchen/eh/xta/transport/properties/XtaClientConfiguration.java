package de.muenchen.eh.xta.transport.properties;

import de.muenchen.eh.xta.tls.TlsClientConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Setter
@ConfigurationProperties(prefix = "bebpo.xta")
public class XtaClientConfiguration {

    /**
     * URI management-services.
     */
    private String managementPortUri;

    /**
     * URI send-services.
     */
    private String sendPortUri;

    /**
     * URI message-box-services.
     */
    private String msgBoxportUri;

    private TlsClientConfiguration tls;

    private PartyIdentifierConfiguration partyIdentifier;

    private MessageTypeConfiguration messageType;
}
