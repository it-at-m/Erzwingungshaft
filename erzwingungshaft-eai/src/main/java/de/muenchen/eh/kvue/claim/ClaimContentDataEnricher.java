package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimContentDataEnricher implements Processor {

    private final LogServiceClaim logServiceClaim;
    private final ClaimDocumentRepository claimDocumentRepository;

    @Override
    public void process(Exchange exchange) {

        try {

            ClaimProcessingContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
            ClaimContentContainerFactory contentContainerFactory = new ClaimContentContainerFactory(dataWrapper, claimDocumentRepository);
            dataWrapper.setContentContainer(contentContainerFactory.supplyContentContainer());

            logServiceClaim.logContent(exchange);
        }
        catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage(), e);
        }
    }

}
