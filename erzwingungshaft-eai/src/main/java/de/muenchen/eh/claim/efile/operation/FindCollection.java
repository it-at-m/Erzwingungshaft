package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.db.entity.Claim;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.db.service.ClaimService;
import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.log.StatusProcessingType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class FindCollection extends EfileOperation {

    @Setter
    @Getter
    private Optional<ReadApentryAntwortDTO> collectionCache = Optional.empty();
    private final Object cacheLock = new Object();

    private final ClaimService claimService;

    public FindCollection(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim,
            ClaimEfileRepository claimEfileRepository, ClaimService claimService) {
        super(operationIdFactory, logServiceClaim, claimEfileRepository);
        this.claimService = claimService;
    }

    @Override
    public void execute(Exchange exchange) {
        findCollectionByGpId(exchange);
    }

    private void findCollectionByGpId(Exchange exchange) {
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

            synchronized (cacheLock) {

                if (collectionCache.isEmpty()) {
                    if (updateCollectionCache(exchange)) return;
                }

                if (collectionCache.isEmpty()) return;

                final long gpId = Long.parseLong(processingDataWrapper.getClaimImport().getGeschaeftspartnerId());

                collectionCache.ifPresent(collection -> {

                    List<Objektreferenz> filteredCollections = gpIdFilter(collection.getGiobjecttype(), gpId);

                    if (filteredCollections.isEmpty()) {
                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND, MessageType.WARN, exchange);

                        Exchange createNewCollectionRequest = operationIdFactory.createExchange(OperationId.SUBJECT_AREA_UNITS, exchange);
                        Exchange createNewCollectionResponse = efileConnector.send(createNewCollectionRequest);
                        if (createNewCollectionResponse.isRouteStop()) {
                            exchange.setRouteStop(true);
                            return;
                        }

                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_ADDED, MessageType.INFO, exchange);

                        Exchange readCollectionRequest = operationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);
                        Exchange efileCollectionResponse = efileConnector.send(readCollectionRequest);
                        if (efileCollectionResponse.isRouteStop()) {
                            exchange.setRouteStop(true);
                            collectionCache = Optional.empty();
                            return;
                        }

                        ReadApentryAntwortDTO updatedApentriese = efileCollectionResponse.getMessage().getBody(ReadApentryAntwortDTO.class);

                        filteredCollections = gpIdFilter(updatedApentriese.getGiobjecttype(), gpId);

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
                        processingDataWrapper.setClaimEfile(createUpdateClaimEfile(exchange, OperationId.READ_COLLECTIONS));
                        logServiceClaim.writeGenericClaimLogMessage(
                                StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_FOUND, MessageType.INFO, exchange);
                    }

                });

            }
        }
    }

    private boolean updateCollectionCache(Exchange exchange) {

        Exchange readCollectionRequest = operationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);
        Exchange efileCollectionResponse = efileConnector.send(readCollectionRequest);
        if (efileCollectionResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return true;
        }
        collectionCache = Optional.ofNullable(efileCollectionResponse.getMessage().getBody(ReadApentryAntwortDTO.class));
        return false;
    }

    private List<Objektreferenz> gpIdFilter(List<Objektreferenz> objektList, long gpid) {

        return objektList.stream()
                .filter(obj -> {

                    if (obj.getObjname() == null || obj.getObjname().isBlank())
                        return false;

                    String objname = obj.getObjname();
                    String[] parts = objname.split("/");

                    String rangePart = parts[parts.length - 1];
                    String[] range = rangePart.split("-");
                    if (range.length != 2) {
                        return false;
                    }
                    try {
                        long gpidVon = Long.parseLong(range[0].trim());
                        long gpidBis = Long.parseLong(range[1].trim());
                        return gpid >= gpidVon && gpid <= gpidBis;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

    }

    public void clearCollectionCache() {

        synchronized (cacheLock) {
            collectionCache = Optional.empty();
        }
    }
}
