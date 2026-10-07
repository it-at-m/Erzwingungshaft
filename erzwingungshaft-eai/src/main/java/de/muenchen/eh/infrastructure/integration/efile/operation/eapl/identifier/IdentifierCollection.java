package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.identifier;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.domain.identifier.IdentifierContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.integration.efile.EfileRouteBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.AbstractEAPLExecute;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.GeschaeftspartnerIdFilter;
import de.muenchen.eh.infrastructure.integration.efile.properties.FileProperties;
import de.muenchen.eh.infrastructure.log.LogServiceIdentifier;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
public class IdentifierCollection extends AbstractEAPLExecute {

    /*
     * Optimize efile requests with collection cache.
     */
    @Setter
    @Getter
    private Optional<ReadApentryAntwortDTO> collectionCache = Optional.empty();
    private final Object cacheLock = new Object();

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    private final IdentifierOperationIdFactory identifierOperationIdFactory;
    private final LogServiceIdentifier logServiceIdentifier;
    private final FileProperties fileProperties;

    public IdentifierCollection(IdentifierOperationIdFactory identifierOperationIdFactory, LogServiceIdentifier logServiceIdentifier,
            FileProperties fileProperties) {
        this.identifierOperationIdFactory = identifierOperationIdFactory;
        this.logServiceIdentifier = logServiceIdentifier;
        this.fileProperties = fileProperties;
    }

    @Override
    protected void execute(Exchange exchange) {
        findCollectionByGpId(exchange);
    }

    private void findCollectionByGpId(Exchange exchange) {

        IdentifierContentWrapper identifierContentWrapper = exchange.getMessage().getBody(IdentifierContentWrapper.class);

        synchronized (cacheLock) {

            if (collectionCache.isEmpty()) {
                // Read eFile collections only once, then search for collections in cache
                if (updateCollectionCache(exchange)) return;
            }

            if (collectionCache.isEmpty()) return;

            final long gpId = Long.parseLong(identifierContentWrapper.getEfileIdentifier().getGeschaeftspartnerId());

            collectionCache.ifPresent(collection -> {

                // Search gpid in collection cache
                List<Objektreferenz> filteredCollections = GeschaeftspartnerIdFilter.gpIdFilter(collection.getGiobjecttype(), gpId);

                if (filteredCollections.isEmpty()) {

                    logServiceIdentifier.writeError(StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND, exchange);
                    exchange.setRouteStop(true);

                } else if (filteredCollections.size() > 1) {

                    logServiceIdentifier.writeError(StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_AMBIGUOUS, exchange);
                    exchange.setRouteStop(true);

                } else {

                    // Set collection reference
                    Objektreferenz efileCollection = filteredCollections.getFirst();
                    identifierContentWrapper.getEfile().put(OperationId.READ_COLLECTIONS.name(), efileCollection);
                    identifierContentWrapper.getEfileIdentifier().setFileCollectionCooAddress(efileCollection.getObjaddress());

                    // Set last valid status
                    identifierContentWrapper.getEfileIdentifier().setMessageType(MessageType.INFO);
                    identifierContentWrapper.getEfileIdentifier().setMessage(StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_FOUND.getDescriptor());

                }
            });
        }
    }

    private boolean updateCollectionCache(Exchange exchange) {

        Exchange readCollectionRequest = identifierOperationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);
        Exchange efileCollectionResponse = efileConnector.send(readCollectionRequest);
        if (efileCollectionResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return true;
        }

        collectionCache = Optional.ofNullable(efileCollectionResponse.getMessage().getBody(ReadApentryAntwortDTO.class));

        // Filter department collections
        collectionCache.ifPresent(cacheContent -> {
            if (cacheContent.getGiobjecttype() != null && fileProperties.getBasenr() != null && !fileProperties.getBasenr().isBlank()) {
                List<Objektreferenz> collectionsStartWithBasenr = cacheContent.getGiobjecttype().stream()
                        .filter(col -> (col.getObjname() != null && col.getObjname().startsWith(fileProperties.getBasenr()))).toList();
                cacheContent.setGiobjecttype(collectionsStartWithBasenr);
            }
        });
        return false;
    }

    public void clearCollectionCache() {

        synchronized (cacheLock) {
            collectionCache = Optional.empty();
        }
    }

}
