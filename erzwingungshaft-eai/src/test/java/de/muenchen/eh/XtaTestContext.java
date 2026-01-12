package de.muenchen.eh;

import de.muenchen.eh.claim.xta.tls.TlsClientParametersFactory;
import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import de.xoev.transport.xta._211.ManagementPortType;
import de.xoev.transport.xta._211.SendPortType;
import org.apache.camel.Configuration;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(TestConstants.SPRING_TEST_PROFILE)
public class XtaTestContext {

    @Bean
    public CxfEndpoint managementPort(XtaClientConfiguration xtaClientConfig, WsAdminLanTestClientConfigurer wsAdminLanTestClientConfigurer) {

        CxfEndpoint mp = new CxfEndpoint();
        mp.setAddress(xtaClientConfig.getManagementPortUri());
        mp.setServiceClass(ManagementPortType.class);

        mp.setCxfConfigurer(wsAdminLanTestClientConfigurer);

        mp.getFeatures().add(new LoggingFeature());
        mp.getFeatures().add(new WSAddressingFeature());
        mp.setMtomEnabled(true);

        mp.setDataFormat(DataFormat.POJO);

        return mp;
    }

    @Bean
    public CxfEndpoint sendPort(XtaClientConfiguration xtaClientConfig, WsAdminLanTestClientConfigurer wsAdminLanTestClientConfigurer) {

        CxfEndpoint sendp = new CxfEndpoint();
        sendp.setAddress(xtaClientConfig.getSendPortUri());
        sendp.setServiceClass(SendPortType.class);

        sendp.setCxfConfigurer(wsAdminLanTestClientConfigurer);

        sendp.getFeatures().add(new LoggingFeature());
        sendp.getFeatures().add(new WSAddressingFeature());
        sendp.setMtomEnabled(true);

        sendp.setDataFormat(DataFormat.POJO);

        return sendp;
    }

    @Bean
    public WsAdminLanTestClientConfigurer wsAdminLanTestClientConfigurer(TlsClientParametersFactory tlsClientParametersFactory) {
        return new WsAdminLanTestClientConfigurer(tlsClientParametersFactory);
    }

}
