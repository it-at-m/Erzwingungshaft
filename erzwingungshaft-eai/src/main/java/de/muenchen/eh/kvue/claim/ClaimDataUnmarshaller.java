package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.EhServiceClaim;
import de.muenchen.eh.log.db.entity.ImportEntity;
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

    ClaimDataWrapper ehDataWrapper;

    public void process(Exchange exchange) {

        ehDataWrapper = new ClaimDataWrapper();

        ehDataWrapper.setImportEntity(exchange.getMessage().getBody(ImportEntity.class));

        Exchange unmarshalledEhClaimData = unmarshallClaimData(exchange);
        ehDataWrapper.setEhClaimData(unmarshalledEhClaimData.getMessage().getBody(ClaimData.class));

        exchange.getMessage().setBody(ehDataWrapper);

        ehServiceClaim.logEntry(exchange);
    }

    private Exchange unmarshallClaimData(Exchange exchange) {
        Exchange marshalContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(ehDataWrapper.getImportEntity().getContent()).build();
        return unmarshalProducer.send(marshalContent);
    }
}
