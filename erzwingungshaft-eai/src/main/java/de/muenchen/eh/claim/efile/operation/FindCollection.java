package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.Claim;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import de.muenchen.eh.log.db.service.ClaimService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class FindCollection extends EfileOperation {

    private Optional<ReadApentryAntwortDTO> collectionCache = Optional.empty();

    private final ClaimService claimService;

    public FindCollection(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository,
            ClaimService claimService) {
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
            if (collectionCache.isEmpty()) {
                Exchange readCollectionRequest = operationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);
                Exchange efileCollectionResponse = efileConnector.send(readCollectionRequest);
                if (efileCollectionResponse.isRouteStop()) {
                    exchange.setRouteStop(true);
                    return;
                }
                collectionCache = Optional.ofNullable(efileCollectionResponse.getMessage().getBody(ReadApentryAntwortDTO.class));
            }

            collectionCache.ifPresent(collection -> {
                List<Objektreferenz> filteredCollections = gpIdFilter(collection.getGiobjecttype(),
                        Long.valueOf(processingDataWrapper.getClaimImport().getGeschaeftspartnerId()));
                if (filteredCollections.isEmpty()) {
                    logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND, MessageType.ERROR,
                            exchange);
                    exchange.setRouteStop(true);
                } else if (filteredCollections.size() > 1) {
                    logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_AMBIGUOUS, MessageType.ERROR,
                            exchange);
                    exchange.setRouteStop(true);
                } else {
                    processingDataWrapper.getEfile().put(OperationId.READ_COLLECTIONS.name(), filteredCollections.getFirst());
                    processingDataWrapper.setClaimEfile(createUpdateClaimEfile(exchange, OperationId.READ_COLLECTIONS));
                    logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_GESCHAEFTSPARTNERID_COLLECTION_FOUND, MessageType.INFO, exchange);
                }
            });
        }
    }

    private List<Objektreferenz> gpIdFilter(List<Objektreferenz> objektList, long gpid) {
        return objektList.stream()
                .filter(obj -> {
                    String objname = obj.getObjname();
                    String[] parts = objname.split("/");
                    if (parts.length != 3) {
                        return false;
                    }
                    String rangePart = parts[2];
                    String[] range = rangePart.split("-");
                    if (range.length != 2) {
                        return false;
                    }
                    try {
                        long gpidVon = Long.parseLong(range[0]);
                        long gpidBis = Long.parseLong(range[1]);
                        return gpid >= gpidVon && gpid <= gpidBis;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public void clearCollectionCache() {
        collectionCache = Optional.empty();
    }

}
