package de.muenchen.eh.kvue.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.DocumentName;
import de.muenchen.eh.kvue.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

@RequiredArgsConstructor
abstract class EfileOperation {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    protected final OperationIdFactory operationIdFactory;

    protected final LogServiceClaim logServiceClaim;

    protected final ClaimEfileRepository claimEfileRepository;

    protected abstract void execute(Exchange exchange);

    protected ClaimEfile createUpdateClaimEfile(final Exchange exchange, final OperationId operationId) {

        ClaimProcessingContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);

        ClaimEfile claimEfile;
        if (dataWrapper.getClaimEfile() == null) {
            claimEfile = new ClaimEfile();
            claimEfile.setClaim(dataWrapper.getClaim());
        } else {
            claimEfile = dataWrapper.getClaimEfile();
        }

        switch (operationId) {
        case READ_COLLECTIONS -> {
            claimEfile.setCollection(((Objektreferenz) dataWrapper.getEfile().get(operationId.name())).getObjaddress());
        }
        case CREATE_FILE -> {
            claimEfile.setFile(((DmsObjektResponse) dataWrapper.getEfile().get(operationId.name())).getObjid());
        }
        case CREATE_FINE -> {
            claimEfile.setFine(((DmsObjektResponse) dataWrapper.getEfile().get(operationId.name())).getObjid());
        }
        case CREATE_OUTGOING -> {
            CreateOutgoingAntwortDTO outgoing = ((CreateOutgoingAntwortDTO) dataWrapper.getEfile().get(operationId.name()));
            claimEfile.setOutgoing(outgoing.getObjid());
            outgoing.getGiobjecttype().forEach(doc -> {
                if (doc.getObjname().equals(DocumentName.ANTRAG.getDescriptor())) {
                    claimEfile.setAntragDocument(doc.getObjaddress());
                } else {
                    claimEfile.setBescheidDocument(doc.getObjaddress());
                }
            });
        }
        default -> {
            exchange.setException(
                    new IllegalArgumentException("Unknown openapi.operationId : ".concat((String) exchange.getMessage().getHeader(Constants.OPERATION_ID))));
        }
        }
        return claimEfileRepository.save(claimEfile);
    }

    @PostConstruct
    public void init() {
        if (!efileConnector.getCamelContext().isStarted()) {
            efileConnector.getCamelContext().start();
        }
    }

}
