package de.muenchen.eh.claim.xta.transport;

import de.muenchen.eh.claim.xta.XtaRouteBuilder;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XtaMessageId {

    private final CamelContext camelContext;

    @Produce(XtaRouteBuilder.BEPBO_MANAGEMENT_PORT)
    private ProducerTemplate managementPort;

    private Exchange buildRequest() {

        return   ExchangeBuilder.anExchange(camelContext)
                .withBody(Collections.emptyList())
                .withHeader(CxfConstants.OPERATION_NAME, "createMessageId")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

    };

    public String create() {

        Exchange response = managementPort.send(buildRequest());
        AttributedURIType attributedURIType = response.getIn().getBody(AttributedURIType.class);
        return attributedURIType.getValue();

    }

}
