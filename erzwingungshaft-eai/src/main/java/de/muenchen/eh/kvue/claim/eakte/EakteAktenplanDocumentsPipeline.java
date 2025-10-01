package de.muenchen.eh.kvue.claim.eakte;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class EakteAktenplanDocumentsPipeline implements Processor {

    @Produce(value= EakteRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    private final EakteOperationIdFactory eakteOperationIdFactory;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);

        Exchange readApentryRequest = eakteOperationIdFactory.createExchange(OperationId.READ_APENTRY, exchange.getProperty(Constants.CLAIM));
        Exchange eakteApentryResponse = eakteConnector.send(readApentryRequest);

        log.debug("---->>> " + eakteApentryResponse.getMessage().getBody());

    }
}
