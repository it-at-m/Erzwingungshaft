package de.muenchen.erzwingungshaft.xta.config;

import de.muenchen.erzwingungshaft.xta.dto.XtaBusinessScenario;
import de.muenchen.erzwingungshaft.xta.dto.XtaIdentifier;
import de.muenchen.erzwingungshaft.xta.dto.XtaMessageMetaData;
import de.muenchen.erzwingungshaft.xta.dto.XtaMessageType;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URL;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

@Getter
@Validated
@Setter
@ConfigurationProperties(prefix = "bebpo.xta")
public class XtaClientConfig {

    /**
     * URI des Management-Services.
     */
    @NotEmpty
    private String managementPortUri;

    /**
     * URI des Send-Services.
     */
    @NotEmpty
    private String sendPortUri;

    /**
     * URI des Message-Box-Services.
     */
    @NotEmpty
    private String msgBoxportUri;

    /**
     * Keystore für die Client-Authentifizierung.
     * Enthält privaten Schlüssel und Zertifikatskette, die der Server vertraut.
     */
    @Valid
    private final KeyStore clientCertKeystore = null;

    /**
     * Truststore für die Server-Authentifizierung.
     * Enthält Root-Zertifikat oder Zertifikatskette des Servers.
     * Kann null sein, falls das Zertifikat von der JVM vertraut wird.
     */
    @Valid
    private final KeyStore trustStore = null;

    public boolean isTrustAll()  {
        return trustStore == null;
    }

    /**
     * Client-Identifikatoren zum Abruf von Nachrichten.
     * Typ ist standardmäßig "xoev".
     */
    private final List<@Valid XtaIdentifier> clientIdentifiers = emptyList();

    /**
     * Optionaler Filter für Nachrichten vor dem Abruf.
     *      * Wenn null, werden alle Nachrichten geholt.
     */
    private final Predicate<XtaMessageMetaData> messageMetaDataFilter = null;

    /**
     * Set Business Scenario!
     */
    @NotEmpty
    private XtaBusinessScenario businessScenario;

    /**
     * Set MessageType!
     */
    @NotEmpty
    private XtaMessageType messageType;

    /**
     * Anzahl der Metadaten-Elemente pro Abruf.
     */
    @Positive
    private final int maxListItems = 50;

    /**
     * Ob das XTA-SOAP-Schema validiert wird.
     */
    @Valid
    private final boolean schemaValidation = true;

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
                    ", storePassword=" + (storePassword.length == 0 ? "EMPTY" : "********")  +
                    ", keyPassword=" + (keyPassword == null ? "STORE_PASSWORD" : "********")  +
                    '}';
        }
    }
}

