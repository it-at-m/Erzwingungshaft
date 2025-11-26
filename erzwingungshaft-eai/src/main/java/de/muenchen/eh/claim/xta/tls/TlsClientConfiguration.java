package de.muenchen.eh.claim.xta.tls;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Setter
public class TlsClientConfiguration {

    /**
     * Keystore for client authentication.
     * Contains the private key and certificate chain trusted by the server.
     */
    @Valid
    private KeyStore clientCertKeystore;

    /**
     * Truststore for server authentication.
     * Contains the server's root certificate or certificate chain.
     * Can be null if the certificate is trusted by the JVM.
     */
    @Valid
    private KeyStore trustStore = null;

    public boolean isTrustAll() {
        return trustStore != null;
    }

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
