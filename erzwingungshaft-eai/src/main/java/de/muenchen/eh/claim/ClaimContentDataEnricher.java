package de.muenchen.eh.claim;

import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
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

            ClaimContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
            ClaimContentContainerFactory contentContainerFactory = new ClaimContentContainerFactory(dataWrapper, claimDocumentRepository);
            dataWrapper.setContentContainer(contentContainerFactory.supplyContentContainer());

            logServiceClaim.logContent(exchange);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage(), e);
        }
    }

}
