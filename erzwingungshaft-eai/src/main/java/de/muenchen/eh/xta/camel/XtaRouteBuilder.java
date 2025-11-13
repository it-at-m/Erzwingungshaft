package de.muenchen.eh.xta.camel;

import de.muenchen.eh.BaseRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class XtaRouteBuilder extends BaseRouteBuilder {

   @Override
    public void configure()  {

       super.configure();

       from("direct:managementPort").routeId("xta-managment-port")
               .to("cxf:bean:managementPort");
    }
}
