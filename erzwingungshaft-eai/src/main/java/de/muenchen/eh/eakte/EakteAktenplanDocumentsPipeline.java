package de.muenchen.eh.eakte;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EakteAktenplanDocumentsPipeline implements Processor {

    @Produce(value= EakteRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    private final EakteOperationIdFactory eakteOperationIdFactory;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimProcessingContentWrapper proccessingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);

        Exchange readApentryRequest = eakteOperationIdFactory.createExchange(OperationId.READ_APENTRY);
        readApentryRequest.setProperty(Constants.CLAIM, proccessingDataWrapper.getClaim());
        Exchange eakteApentryResponse = eakteConnector.send(readApentryRequest);

        eakteApentryResponse.toString();

    }
}
