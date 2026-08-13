package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.Claim;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.ClaimEfileRepository;
import de.muenchen.eh.infrastructure.db.service.ClaimService;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.GeschaeftspartnerIdFilter;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * collection.getGiobjecttype() provides an array list.
 * The purpose of the collectionCache is to read the eFile collections for storing the fine
 * proceedings files only once and not to execute them again for each proceeding.
 * The list of efile collections is not changed after reading. Synchronized ensures that only one
 * thread at a time has access to the collections list.
 * If the claim-eh-process route is parallelized, this should not cause any problems.
 */

@Component
@Log4j2
public class AddCollection extends AbstractEAPLOperation {

    /*
     * Optimize efile requests with collection cache.
     */
    @Setter
    @Getter
    private Optional<ReadApentryAntwortDTO> collectionCache = Optional.empty();
    private final Object cacheLock = new Object();

    private final ClaimService claimService;

    public AddCollection(CompleteOperationIdFactory completeOperationIdFactory, LogServiceClaim logServiceClaim,
            ClaimEfileRepository claimEfileRepository, ClaimService claimService) {
        super(completeOperationIdFactory, logServiceClaim, claimEfileRepository);
        this.claimService = claimService;
    }

    @Override
    public void execute(Exchange exchange) {
        findCollectionByGpId(exchange);
    }

    private void findCollectionByGpId(Exchange exchange) {
        /*
         * Check GP-ID already registered in db.
         */
        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        List<Claim> gpClaimEfiles = claimService.claimEfilesWithCorrespondingGId(processingDataWrapper.getClaimImport().getGeschaeftspartnerId());

        if (!gpClaimEfiles.isEmpty()) {
            ClaimEfile claimEfile = new ClaimEfile();
            claimEfile.setClaim(processingDataWrapper.getClaim());
            claimEfile.setCollection(gpClaimEfiles.getLast().getClaimEfile().getCollection());
            claimEfile.setFile(gpClaimEfiles.getLast().getClaimEfile().getFile());
            processingDataWrapper.setClaimEfile(claimEfileRepository.save(claimEfile));
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_GPID_COLLECTION_READ_FROM_DB, MessageType.INFO, exchange);
        } else {

            /*
             * Check if file exists in efile.
             */
            synchronized (cacheLock) {

                if (collectionCache.isEmpty()) {
                    if (updateCollectionCache(exchange)) return;
                }

                if (collectionCache.isEmpty()) return;

                final long gpId = Long.parseLong(processingDataWrapper.getClaimImport().getGeschaeftspartnerId());

                collectionCache.ifPresent(collection -> {

                    List<Objektreferenz> filteredCollections = GeschaeftspartnerIdFilter.gpIdFilter(collection.getGiobjecttype(), gpId);

                    // Create new Collection
                    if (filteredCollections.isEmpty()) {
                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND, MessageType.WARN, exchange);

                        Exchange createNewCollectionRequest = completeOperationIdFactory.createExchange(OperationId.SUBJECT_AREA_UNITS, exchange);
                        Exchange createNewCollectionResponse = efileConnector.send(createNewCollectionRequest);
                        if (createNewCollectionResponse.isRouteStop()) {
                            exchange.setRouteStop(true);
                            return;
                        }

                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_ADDED, MessageType.INFO, exchange);

                        Exchange readCollectionRequest = completeOperationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);
                        Exchange efileCollectionResponse = efileConnector.send(readCollectionRequest);
                        if (efileCollectionResponse.isRouteStop()) {
                            exchange.setRouteStop(true);
                            collectionCache = Optional.empty();
                            return;
                        }

                        ReadApentryAntwortDTO updatedApentriese = efileCollectionResponse.getMessage().getBody(ReadApentryAntwortDTO.class);

                        filteredCollections = GeschaeftspartnerIdFilter.gpIdFilter(updatedApentriese.getGiobjecttype(), gpId);

                        collectionCache = Optional.of(updatedApentriese);
                    }

                    if (filteredCollections.isEmpty()) {
                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND, MessageType.ERROR, exchange);
                        exchange.setRouteStop(true);

                    } else if (filteredCollections.size() > 1) {
                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_AMBIGUOUS, MessageType.ERROR, exchange);
                        exchange.setRouteStop(true);

                    } else {
                        processingDataWrapper.getEfile().put(OperationId.READ_COLLECTIONS.name(), filteredCollections.getFirst());
                        processingDataWrapper.setClaimEfile(persistClaimEfileReference(exchange, OperationId.READ_COLLECTIONS));
                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_FOUND, MessageType.INFO, exchange);
                    }

                });

            }
        }
    }

    private boolean updateCollectionCache(Exchange exchange) {

        Exchange readCollectionRequest = completeOperationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);
        Exchange efileCollectionResponse = efileConnector.send(readCollectionRequest);
        if (efileCollectionResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return true;
        }
        collectionCache = Optional.ofNullable(efileCollectionResponse.getMessage().getBody(ReadApentryAntwortDTO.class));
        return false;
    }

    public void clearCollectionCache() {

        synchronized (cacheLock) {
            collectionCache = Optional.empty();
        }
    }
}
