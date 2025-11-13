package de.muenchen.eh.xta.camel;

import de.muenchen.eh.xta.config.XtaClientConfig;
import org.apache.camel.Configuration;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.springframework.context.annotation.Bean;

@Configuration
public class XtaContext {

    @Bean
    public CxfEndpoint managementPort(XtaClientConfig xtaClientConfig) {

        CxfEndpoint mp = new CxfEndpoint();
        mp.setAddress(xtaClientConfig.getManagementPortUri());
        mp.setServiceClass(genv3.de.xoev.transport.xta.x211.ManagementPortType.class);
        return mp;
    }

    @Bean
    public CxfEndpoint msgBoxPort(XtaClientConfig xtaClientConfig) {

        CxfEndpoint msgbp = new CxfEndpoint();
        msgbp.setAddress(xtaClientConfig.getMsgBoxportUri());
        msgbp.setServiceClass(genv3.de.xoev.transport.xta.x211.MsgBoxPortType.class);
        return msgbp;
    }

    @Bean
    public CxfEndpoint sendPort(XtaClientConfig xtaClientConfig) {

        CxfEndpoint sendp = new CxfEndpoint();
        sendp.setAddress(xtaClientConfig.getSendPortUri());
        sendp.setServiceClass(genv3.de.xoev.transport.xta.x211.SendPortType.class);
        return sendp;
    }

}
