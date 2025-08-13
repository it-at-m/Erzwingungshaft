package de.muenchen.erzwingungshaft.xta.config;

import de.muenchen.erzwingungshaft.xta.dto.XtaMessageMetaData;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.net.URL;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

@Getter
@Configuration
@EnableConfigurationProperties(XtaClientConfig.class)
@Validated
@ConfigurationProperties(prefix = "bebpo.xta")
@Setter
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

    /**
     * Client-Identifikatoren zum Abruf von Nachrichten.
     * Typ ist standardmäßig "xoev".
     */
    private final List<@Valid XtaIdentifier> clientIdentifiers = emptyList();

    /**
     * Optionaler Filter für Nachrichten vor dem Abruf.
     * Wenn null, werden alle Nachrichten geholt.
     */
    private final Predicate<XtaMessageMetaData> isMessageSupported = null;

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
     * Ob SOAP-Requests geloggt werden.
     */
    private final boolean logSoapRequests = false;

    /**
     * Ob SOAP-Responses geloggt werden.
     */
    private final boolean logSoapResponses = false;

    /**
     * Konfiguration für einen Keystore (Client-Zertifikat oder Truststore).
     */
    public record KeyStore(
            @NotNull URL url,
            @NotBlank String type,
            @NotNull char[] password
    ) {
        @Override
        public String toString() {
            return "KeyStore{" +
                    "password=" + (password.length == 0 ? "EMPTY" : "********") +
                    ", url=" + url +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    /**
     * XTA-Party-Identifier für Clients oder Server.
     */
    public record XtaIdentifier(
            @Nullable String name,
            @Nullable String category,
            @NotBlank String value
    ) {
    }
}

