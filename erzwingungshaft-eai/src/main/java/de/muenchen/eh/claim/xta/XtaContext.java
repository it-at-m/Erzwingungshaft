package de.muenchen.eh.claim.xta;

import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import genv3.de.xoev.transport.xta.x211.ManagementPortType;
import genv3.de.xoev.transport.xta.x211.SendPortType;
import org.apache.camel.Configuration;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;

@Configuration
public class XtaContext {

    @Bean
    public CxfEndpoint managementPort(XtaClientConfiguration xtaClientConfig) {

        CxfEndpoint mp = new CxfEndpoint();
        mp.setAddress(xtaClientConfig.getManagementPortUri());
        mp.setServiceClass(ManagementPortType.class);

        mp.getFeatures().add(new LoggingFeature());
        mp.getFeatures().add(new WSAddressingFeature());
        mp.setMtomEnabled(true);

        mp.setDataFormat(DataFormat.POJO);

        return mp;
    }

    @Bean
    public CxfEndpoint sendPort(XtaClientConfiguration xtaClientConfig) {

        CxfEndpoint sendp = new CxfEndpoint();
        sendp.setAddress(xtaClientConfig.getSendPortUri());
        sendp.setServiceClass(SendPortType.class);

        sendp.getFeatures().add(new LoggingFeature());
        sendp.getFeatures().add(new WSAddressingFeature());
        sendp.setMtomEnabled(true);

         sendp.setDataFormat(DataFormat.POJO);

        return sendp;
    }

}
