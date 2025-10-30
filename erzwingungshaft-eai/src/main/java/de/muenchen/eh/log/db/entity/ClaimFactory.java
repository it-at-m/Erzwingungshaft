package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.IClaimEntity;
import java.util.Optional;
import org.apache.camel.Exchange;

public class ClaimFactory {

    public static IClaimEntity configureEntity(IClaimEntity entity, Exchange exchange) {

        entity.setClaimId(claimFacade(exchange).getId());
        return entity;
    }

    public static Claim claimFacade(Exchange exchange) {
        Optional<Claim> entryOptional = Optional.ofNullable(exchange.getProperty(Constants.CLAIM, Claim.class));
        return entryOptional.orElseThrow(() -> new RuntimeException(
                "No EntryEntity found. The required EntryEntity is initially created with the 'EhService.logEntry' call. This must be executed first."));

    }
}
