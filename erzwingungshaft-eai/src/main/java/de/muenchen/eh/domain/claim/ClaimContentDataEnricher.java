package de.muenchen.eh.domain.claim;

import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import de.muenchen.eh.infrastructure.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import java.util.Optional;
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
    private final FineIdentifier fineIdentifier;

    @Override
    public void process(Exchange exchange) {

        try {

            ClaimContentWrapper claimContentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

            // Check 'geschaeftsparterId' + 'kassenzeichen' has valid identifier
            Optional<EfileIdentifier> efileIdentifier = fineIdentifier.exists(exchange);

            if (efileIdentifier.isEmpty()) {
                exchange.setRouteStop(true);
                return;
            }

            claimContentWrapper.setEfileIdentifier(efileIdentifier.get());

            // Prepare xjustiz xml
            ClaimContentContainerFactory contentContainerFactory = new ClaimContentContainerFactory(claimContentWrapper, claimDocumentRepository);
            claimContentWrapper.setContentContainer(contentContainerFactory.supplyContentContainer());

            logServiceClaim.logContent(exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage(), e);
        }
    }

}
