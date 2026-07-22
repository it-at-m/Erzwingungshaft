package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.addoutgoing;

import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.db.repository.ClaimEfileRepository;
import de.muenchen.eh.infrastructure.db.repository.EfileIdentifierRepository;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("eAPLAddOutGoingExecutor")
@RequiredArgsConstructor
public class EAPLAddOutGoingExecutor implements Processor {

    private final EAPLAddOutgoing addOutgoing;
    private final EfileIdentifierRepository efileIdentifierRepository;
    private final ClaimEfileRepository claimEfileRepository;
    private final LogServiceClaim logServiceClaim;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimContentWrapper claimContentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

        // Persist efile collection, file and fine coo address
        ClaimEfile claimEfile;
        if (claimContentWrapper.getClaimEfile() == null) {
            claimEfile = new ClaimEfile();
            claimEfile.setClaim(claimContentWrapper.getClaim());
        } else {
            claimEfile = claimContentWrapper.getClaimEfile();
        }
        claimEfile.setClaim(claimContentWrapper.getClaim());
        claimEfile.setFile(claimContentWrapper.getEfileIdentifier().getFileCooAddress());
        claimEfile.setCollection(claimContentWrapper.getEfileIdentifier().getFileCollectionCooAddress());
        claimEfile.setFine(claimContentWrapper.getEfileIdentifier().getFineCooAddress());
        claimContentWrapper.setClaimEfile(claimEfileRepository.save(claimEfile));

        // Register dms fine object for ongoing 'outgoing' treatment
        DmsObjektResponse fineObjektResponse = new DmsObjektResponse();
        fineObjektResponse.setObjid(claimContentWrapper.getEfileIdentifier().getFineCooAddress());
        fineObjektResponse.setObjname(claimContentWrapper.getEfileIdentifier().getFineNameCooAddress());

        claimContentWrapper.getEfile().put(OperationId.CREATE_FINE.name(), fineObjektResponse);

        addOutgoing.execute(exchange);

        if (exchange.isRouteStop())
            return;

        // All done. Delete geschaeftsparterId + kassenzeichen entity from identifier table.
        efileIdentifierRepository.delete(claimContentWrapper.getEfileIdentifier());

    }
}
