package de.muenchen.eh.claim.xta;

import de.muenchen.eh.claim.xta.exception.XtaClientInitializationException;
import de.muenchen.eh.claim.xta.tls.TlsClientParametersFactory;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.cxf.jaxws.CxfConfigurer;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;

@RequiredArgsConstructor
public class XtaClientConfigurer implements CxfConfigurer {

    private final TlsClientParametersFactory tlsClientParametersFactory;

    @Override
    public void configure(AbstractWSDLBasedEndpointFactory factoryBean) {
    }

    @Override
    public void configureClient(Client client) {

        HTTPConduit conduit = (HTTPConduit) client.getConduit();

        try {
            conduit.setTlsClientParameters(tlsClientParametersFactory.create());
        } catch (XtaClientInitializationException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void configureServer(Server server) {
    }

}
