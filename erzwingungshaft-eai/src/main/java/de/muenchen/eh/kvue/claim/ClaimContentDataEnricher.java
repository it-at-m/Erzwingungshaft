package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.EhServiceClaim;
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

    private final EhServiceClaim ehServiceClaim;
    private final ClaimDocumentRepository claimDocumentRepository;

    public void process(Exchange exchange) {

        try {

            ClaimProcessingContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
            ClaimContentContainerFactory contentContainerFactory = new ClaimContentContainerFactory(dataWrapper.getEhImportClaimData(), dataWrapper.getClaimImport(), claimDocumentRepository);
            dataWrapper.setContentContainer(contentContainerFactory.supplyContentContainer());

            ehServiceClaim.logContent(exchange);
        }
        catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage(), e);
        }
    }

}
