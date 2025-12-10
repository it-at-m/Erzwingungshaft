package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.DocumentName;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.LogServiceClaim;
import jakarta.annotation.PostConstruct;
import java.util.List;
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

        ClaimContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

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
        case SEARCH_FILE -> {
            claimEfile.setFile(((List<Objektreferenz>) dataWrapper.getEfile().get(operationId.name())).getLast().getObjaddress());
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
                } else if (doc.getObjname().equals(DocumentName.BESCHEID.getDescriptor())) {
                    claimEfile.setBescheidDocument(doc.getObjaddress());
                } else if (doc.getObjname().equals(DocumentName.KOSTEN.getDescriptor())) {
                    claimEfile.setKostendokument(doc.getObjaddress());
                } else if (doc.getObjname().equals(DocumentName.VERWERFUNG.getDescriptor())) {
                    claimEfile.setVerwerfung(doc.getObjaddress());
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
