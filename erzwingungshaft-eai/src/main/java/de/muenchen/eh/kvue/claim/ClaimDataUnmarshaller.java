package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.EhServiceClaim;
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

    private final EhServiceClaim ehServiceClaim;

    @Produce(value = ClaimRouteBuilder.UNMARSHALL_EH_CLAIM_DATA)
    private ProducerTemplate unmarshalProducer;

    ClaimProcessingContentWrapper ehDataWrapper;

    public void process(Exchange exchange) {

        ehDataWrapper = new ClaimProcessingContentWrapper();

        ehDataWrapper.setClaimImport(exchange.getMessage().getBody(ClaimImport.class));

        Exchange unmarshalledEhClaimData = unmarshallClaimData(exchange);
        ehDataWrapper.setEhImportClaimData(unmarshalledEhClaimData.getMessage().getBody(ImportClaimData.class));

        exchange.getMessage().setBody(ehDataWrapper);

        ehServiceClaim.logClaim(exchange);
        ehServiceClaim.logUnmarshall(exchange);

    }

    private Exchange unmarshallClaimData(Exchange exchange) {
        Exchange marshalContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(ehDataWrapper.getClaimImport().getContent()).build();
        return unmarshalProducer.send(marshalContent);
    }
}
