package de.muenchen.eh.xta;

import static java.util.Collections.emptyList;

import de.muenchen.eh.xta.dto.XtaBusinessScenario;
import de.muenchen.eh.xta.dto.XtaIdentifier;
import de.muenchen.eh.xta.dto.XtaMessageMetaData;
import de.muenchen.eh.xta.dto.XtaMessageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Setter
@ConfigurationProperties(prefix = "bebpo.xta")
public class XtaClientConfig {

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

    /**
     * Keystore für die Client-Authentifizierung.
     * Enthält privaten Schlüssel und Zertifikatskette, die der Server vertraut.
     */
    @Valid
    private KeyStore clientCertKeystore;

    /**
     * Truststore für die Server-Authentifizierung.
     * Enthält Root-Zertifikat oder Zertifikatskette des Servers.
     * Kann null sein, falls das Zertifikat von der JVM vertraut wird.
     */
    @Valid
    private KeyStore trustStore = null;

    public boolean isTrustAll() {
        return trustStore != null;
    }

    /**
     * Client-Identifikatoren zum Abruf von Nachrichten.
     * Typ ist standardmäßig "xoev".
     */
    private List<@Valid XtaIdentifier> clientIdentifiers = emptyList();

    /**
     * Optionaler Filter für Nachrichten vor dem Abruf.
     * * Wenn null, werden alle Nachrichten geholt.
     */
    private Predicate<XtaMessageMetaData> messageMetaDataFilter = null;

    /**
     * Set Business Scenario!
     */
    private XtaBusinessScenario businessScenario;

    /**
     * Set MessageType!
     */
    private XtaMessageType messageType;

    /**
     * Anzahl der Metadaten-Elemente pro Abruf.
     */
    @Positive
    private int maxListItems = 50;

    /**
     * Ob das XTA-SOAP-Schema validiert wird.
     */
    @Valid
    private boolean schemaValidation = true;

    /**
     * Soap spezifische Einstellungen - alle mit Defaults vorbelegt.
     */
    private final boolean logSoapRequests = false;
    private final boolean logSoapResponses = false;
    private final long connectionTimeout = 300000L;
    private final long receiveTimeout = 300000L;
    private final long connectionRequestTimeout = 300000L;
    private final String tlsProtocol = "TLSv1.2";

    /**
     * Konfiguration für einen Keystore (Client-Zertifikat oder Truststore).
     */
    public record KeyStore(
            @NotNull URL url,
            @NotBlank String type,
            @NotNull char[] storePassword,
            char[] keyPassword,
            String keyAlias

    ) {

        public char[] effectiveKeyPassword() {
            return (keyPassword == null) ? storePassword : keyPassword;
        }

        @Override
        public String toString() {
            return "KeyStore{" +
                    "url=" + url +
                    ", type='" + type + '\'' +
                    ", storePassword=" + (storePassword.length == 0 ? "EMPTY" : "********") +
                    ", keyPassword=" + (keyPassword == null ? "STORE_PASSWORD" : "********") +
                    '}';
        }
    }
}
