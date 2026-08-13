package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.addoutgoing;

import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete.AddOutgoing;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EAPLAddOutgoing extends EAPLAddOutgoingTemplate {

    private final AddOutgoing addOutgoing;

    @Override
    protected void addOutgoing(Exchange exchange) {
        addOutgoing.execute(exchange);
    }
}
