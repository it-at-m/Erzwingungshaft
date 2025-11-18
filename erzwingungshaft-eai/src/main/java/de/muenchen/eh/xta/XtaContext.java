package de.muenchen.eh.xta;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Configuration;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.validation.DefaultSchemaValidationTypeProvider;
import org.apache.cxf.feature.validation.SchemaValidationFeature;
import org.apache.cxf.feature.validation.SchemaValidationTypeProvider;
import org.springframework.context.annotation.Bean;

@Configuration
public class XtaContext {

    @Bean
    public CxfEndpoint managementPort(XtaClientConfig xtaClientConfig) {

        CxfEndpoint mp = new CxfEndpoint();
        mp.setAddress(xtaClientConfig.getManagementPortUri());

 //       mp.setCxfConfigurer(new WsClientConfigurer());

        mp.setServiceClass(genv3.de.xoev.transport.xta.x211.ManagementPortType.class);

        mp.setDataFormat(DataFormat.CXF_MESSAGE);

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

//    @Bean
    public SpringBus cxfBus() {
        SpringBus bus = new SpringBus();
 //       bus.getFeatures().add(createSchemaValidationFeature());
        bus.setProperty("org.apache.cxf.catalog", "classpath:META-INF/catalog.xml");
        return bus;
    }

    private SchemaValidationFeature createSchemaValidationFeature() {

//        List<URL> schemaUrls = new ArrayList<>();
//        schemaUrls.add(getClass().getClassLoader().getResource("schema1.xsd"));
//        schemaUrls.add(getClass().getClassLoader().getResource("schema2.xsd"));

        Map<String, SchemaValidation.SchemaValidationType> operationMap = new HashMap<>();
        operationMap.put("createOrder", SchemaValidation.SchemaValidationType.OUT);

        SchemaValidationTypeProvider provider =
                new DefaultSchemaValidationTypeProvider(operationMap);

        SchemaValidationFeature feature = new SchemaValidationFeature(provider);

   //     feature.setScemaLocations("classpath:schemas/schema11.xsd,classpath:schemas/ws-addr.xsd");
        return feature;
    }


}
