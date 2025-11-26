package de.muenchen.eh;

import de.muenchen.eh.claim.xta.tls.TlsClientParametersFactory;
import de.muenchen.eh.claim.xta.exception.XtaClientInitializationException;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.cxf.jaxws.CxfConfigurer;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;

@RequiredArgsConstructor
public class WsAdminLanTestClientConfigurer implements CxfConfigurer {

    private final TlsClientParametersFactory tlsClientParametersFactory;

    @Override
    public void configure(AbstractWSDLBasedEndpointFactory factoryBean) {
    }

    @Override
    public void configureClient(Client client) {

        HTTPConduit conduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setProxyServer("127.0.0.1");
        policy.setProxyServerPort(9999);
        policy.setProxyServerType(ProxyServerType.HTTP);

        try {
            conduit.setTlsClientParameters(tlsClientParametersFactory.create());
        } catch (XtaClientInitializationException e) {
            throw new RuntimeException(e);
        }

        conduit.setClient(policy);

    }

    @Override
    public void configureServer(Server server) {
    }
}
