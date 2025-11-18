package de.muenchen.eh.xta;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.cxf.jaxws.CxfConfigurer;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsClientConfigurer implements CxfConfigurer {

    private final TlsClientParametersFactory tlsClientParametersFactory;

    @Override
    public void configure(AbstractWSDLBasedEndpointFactory factoryBean) {

//                Map<String, Object> properties = new HashMap<>();
//        properties.put("http.proxyHost", "127.0.0.1");
//        properties.put("http.proxyPort", "9999");
//        properties.put("https.proxyHost", "10.158.0.85");
//        properties.put("https.proxyPort", "80");


//        factoryBean.getBus().setProperties(properties);

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
