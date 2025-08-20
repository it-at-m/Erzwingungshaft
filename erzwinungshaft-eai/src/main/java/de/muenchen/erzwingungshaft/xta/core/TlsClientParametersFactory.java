package de.muenchen.erzwingungshaft.xta.core;


import de.muenchen.erzwingungshaft.xta.config.XtaClientConfig;
import de.muenchen.erzwingungshaft.xta.exception.XtaClientInitializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.spi.annotations.Component;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.https.httpclient.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TlsClientParametersFactory {

    private final XtaClientConfig config;

    public TLSClientParameters create() throws XtaClientInitializationException {
        log.debug("Creating TLS client parameters using config");

        try {
            TLSClientParameters tlsClientParameters = new TLSClientParameters();

            SSLContext sslContext = buildSslContext();
            tlsClientParameters.setSSLSocketFactory(sslContext.getSocketFactory());

            var hostnameVerifier = (config.isTrustAll())
                    ? new DefaultHostnameVerifier()
                    : new NoopHostnameVerifier();
            tlsClientParameters.setHostnameVerifier(hostnameVerifier);

            return tlsClientParameters;
        } catch (XtaClientInitializationException e) {
            throw new XtaClientInitializationException("TLS configuration failed: " + e.getMessage(), e);
        }
    }

    private SSLContextBuilder createSslContextBuilder() {
        return SSLContextBuilder.create().setProtocol(config.getTlsProtocol());
    }

    private SSLContext buildSslContext() throws XtaClientInitializationException {
        SSLContextBuilder sslContextBuilder = createSslContextBuilder();

        try {
            if (config.isTrustAll()) {
                sslContextBuilder.loadTrustMaterial(
                        config.getTrustStore().url(),
                        config.getTrustStore().storePassword()
                );
                log.debug("Trust store loaded ({})", config.getTrustStore().toString());
            } else {
                log.warn("Using trust-all-strategie! This is not recommended and will disable host name checking!");
                sslContextBuilder.loadTrustMaterial(new TrustAllStrategy());
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
            throw new XtaClientInitializationException("Failed to load Truststore material.", e);
        }

        try {
            if (config.getClientCertKeystore() != null) {
                if (config.getClientCertKeystore().keyAlias() != null) {
                    sslContextBuilder.loadKeyMaterial(
                            config.getClientCertKeystore().url(),
                            config.getClientCertKeystore().storePassword(),
                            config.getClientCertKeystore().effectiveKeyPassword(),
                            (aliases, sslParameters) -> config.getClientCertKeystore().keyAlias()
                    );
                } else {
                    sslContextBuilder.loadKeyMaterial(
                            config.getClientCertKeystore().url(),
                            config.getClientCertKeystore().storePassword(),
                            config.getClientCertKeystore().effectiveKeyPassword()
                    );
                }

                log.debug("Client cert keystore loaded ({})", config.getClientCertKeystore().toString());
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
            throw new XtaClientInitializationException("Failed to load SSL context.", e);
        }

        try {
            return sslContextBuilder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new XtaClientInitializationException("Failed to build SSL context", e);
        }
    }
}
