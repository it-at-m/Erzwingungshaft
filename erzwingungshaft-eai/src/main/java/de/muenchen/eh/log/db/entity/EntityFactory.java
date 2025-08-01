package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.common.Entity;
import de.muenchen.eh.log.Constants;
import org.apache.camel.Exchange;

import java.util.Optional;

public class EntityFactory {

    public static Entity configureEntity(Entity entity, Exchange exchange) {

        entity.setEntryId(entryEntityFacade(exchange).getId());
        return entity;
    }

    public static EntryEntity entryEntityFacade(Exchange exchange) {
        Optional<EntryEntity> entryOptional = Optional.ofNullable(exchange.getIn().getHeader(Constants.ENTRY_ENTITY, EntryEntity.class));
        return entryOptional.orElseThrow(() -> new RuntimeException("No EntryEntity found. The required EntryEntity is initially created with the 'logEntry' call. This must be executed first."));

    }
}
