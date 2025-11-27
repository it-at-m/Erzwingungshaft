package de.muenchen.eh.db.entity;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.db.IClaimEntity;
import java.util.Objects;
import java.util.Optional;
import org.apache.camel.Exchange;

public class ClaimFactory {

    public static IClaimEntity configureEntity(IClaimEntity entity, Exchange exchange) {

        Objects.requireNonNull(entity, "Claim entity must not be NULL.");
        Claim claim = claimFacade(Objects.requireNonNull(exchange, "exchange"));
        entity.setClaimId(claim.getId());
        return entity;
    }

    public static Claim claimFacade(Exchange exchange) {
        Optional<Claim> entryOptional = Optional.ofNullable(exchange.getProperty(Constants.CLAIM, Claim.class));
        return entryOptional.orElseThrow(() -> new IllegalStateException(
                "No Claim found in Exchange properties (" + Constants.CLAIM + "). Ensure EhService.logEntry was executed first."));
    }
}
