package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.IClaimEntity;
import de.muenchen.eh.log.Constants;
import org.apache.camel.Exchange;

import java.util.Optional;

public class ClaimFactory {

    public static IClaimEntity configureEntity(IClaimEntity entity, Exchange exchange) {

        entity.setClaimId(entryEntityFacade(exchange).getId());
        return entity;
    }

    public static ClaimEntity entryEntityFacade(Exchange exchange) {
        Optional<ClaimEntity> entryOptional = Optional.ofNullable(exchange.getMessage().getHeader(Constants.ENTRY_ENTITY, ClaimEntity.class));
        return entryOptional.orElseThrow(() -> new RuntimeException("No EntryEntity found. The required EntryEntity is initially created with the 'EhService.logEntry' call. This must be executed first."));

    }
}
