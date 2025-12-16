package de.muenchen.eh.claim.xta.tls;

import de.muenchen.eh.claim.xta.exception.XtaClientInitializationException;
import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TlsClientParametersFactory {

    private final XtaClientConfiguration clientConfiguration;

    public TLSClientParameters create() throws XtaClientInitializationException {
        log.debug("Creating TLS client parameters using config");

        try {
            TLSClientParameters tlsClientParameters = new TLSClientParameters();

            SSLContext sslContext = buildSSLContext();
            tlsClientParameters.setSSLSocketFactory(sslContext.getSocketFactory());

            //            var hostnameVerifier = (clientConfiguration.getTls().isTrustAll())
            //                    ? new DefaultHostnameVerifier()
            //                    : new NoopHostnameVerifier();
            //
            //            tlsClientParameters.setHostnameVerifier(hostnameVerifier);

            var hostnameVerifier = new NoopHostnameVerifier();
            tlsClientParameters.setHostnameVerifier(hostnameVerifier);

            log.info("Hostname verifier: {}", hostnameVerifier.getClass().getName());

            return tlsClientParameters;
        } catch (XtaClientInitializationException e) {
            throw new XtaClientInitializationException("TLS configuration failed: " + e.getMessage(), e);
        }
    }

    private SSLContextBuilder createSSLContextBuilder() {
        return SSLContextBuilder.create().setProtocol(clientConfiguration.getTls().getTlsProtocol());
    }

    private SSLContext buildSSLContext() throws XtaClientInitializationException {
        SSLContextBuilder sslContextBuilder = createSSLContextBuilder();

        try {

            //            if (clientConfiguration.getTls().isTrustAll()) {
            //                sslContextBuilder.loadTrustMaterial(
            //                        clientConfiguration.getTls().getTrustStore().url(),
            //                        clientConfiguration.getTls().getTrustStore().storePassword());
            //                log.debug("Trust store loaded ({})", clientConfiguration.getTls().getTrustStore().toString());
            //            } else {
            log.warn("Using trust-all-strategie! This is not recommended and will disable host name checking!");
            sslContextBuilder.loadTrustMaterial(new TrustAllStrategy());
            //            }
            //        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {

        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new XtaClientInitializationException("Failed to load Truststore material.", e);
        }

        try {
            if (clientConfiguration.getTls().getClientCertKeystore() != null) {
                if (clientConfiguration.getTls().getClientCertKeystore().keyAlias() != null) {
                    sslContextBuilder.loadKeyMaterial(
                            clientConfiguration.getTls().getClientCertKeystore().url(),
                            clientConfiguration.getTls().getClientCertKeystore().storePassword(),
                            clientConfiguration.getTls().getClientCertKeystore().effectiveKeyPassword(),
                            (aliases, sslParameters) -> clientConfiguration.getTls().getClientCertKeystore().keyAlias());
                } else {
                    sslContextBuilder.loadKeyMaterial(
                            clientConfiguration.getTls().getClientCertKeystore().url(),
                            clientConfiguration.getTls().getClientCertKeystore().storePassword(),
                            clientConfiguration.getTls().getClientCertKeystore().effectiveKeyPassword());
                }

                log.debug("Client cert keystore loaded ({})", clientConfiguration.getTls().getClientCertKeystore().toString());
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
