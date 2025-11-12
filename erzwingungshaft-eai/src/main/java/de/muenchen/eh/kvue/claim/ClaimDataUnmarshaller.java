package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.ClaimImport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimDataUnmarshaller implements Processor {

    private final LogServiceClaim logServiceClaim;

    @Produce(value = ClaimRouteBuilder.UNMARSHALL_EH_CLAIM_DATA)
    private ProducerTemplate unmarshalProducer;

    ClaimProcessingContentWrapper processingDataWrapper;

    public void process(Exchange exchange) {

        processingDataWrapper = new ClaimProcessingContentWrapper();
        processingDataWrapper.setClaimImport(exchange.getMessage().getBody(ClaimImport.class));
        exchange.getMessage().setBody(processingDataWrapper);
        logServiceClaim.logClaim(exchange);

        Exchange unmarshalledEhClaimData = unmarshallClaimData(exchange);
        if (unmarshalledEhClaimData.getAllProperties().get(Exchange.EXCEPTION_CAUGHT) != null) {
            exchange.setRouteStop(true);
            return;
        }
        processingDataWrapper.setEhImportClaimData(unmarshalledEhClaimData.getMessage().getBody(ImportClaimData.class));
        logServiceClaim.logUnmarshall(exchange);

    }

    private Exchange unmarshallClaimData(Exchange exchange) {
        Exchange marshalContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(processingDataWrapper.getClaimImport().getContent())
                .withProperty(Constants.CLAIM, processingDataWrapper.getClaim()).build();
        return unmarshalProducer.send(marshalContent);
    }
}
