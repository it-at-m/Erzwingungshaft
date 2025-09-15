package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.EhServiceClaim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EhClaimContentDataEnricher implements Processor {

    private final EhServiceClaim ehServiceClaim;

    public void process(Exchange exchange) {

        try {
            ehServiceClaim.logUnmarshall(exchange);

            EhClaimDataWrapper dataWrapper = exchange.getMessage().getBody(EhClaimDataWrapper.class);
            EhClaimContentContainerFactory contentContainerFactory = new EhClaimContentContainerFactory(dataWrapper.getEhClaimData(), dataWrapper.getImportEntity());
            dataWrapper.setContentContainer(contentContainerFactory.supplyContentContainer());

            ehServiceClaim.logContent(exchange);
        }
        catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage(), e);
        }
    }

}
