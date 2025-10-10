package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.EfileConstants;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddOutgoing extends EfileOperation {

       private final ClaimEfileRepository claimEfileRepository;

    public AddOutgoing(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository) {
        super(operationIdFactory, logServiceClaim);
        this.claimEfileRepository = claimEfileRepository;
    }

    @Override
    protected void execute(Exchange exchange) {

        Exchange createOutgoingRequest = operationIdFactory.createExchange(OperationId.CREATE_OUTGOING, exchange);
        Exchange createOutgoingResponse = efileConnector.send(createOutgoingRequest);
        if (createOutgoingResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }
        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
        CreateOutgoingAntwortDTO outgoingResponse = (CreateOutgoingAntwortDTO) createOutgoingResponse.getMessage().getBody();
        processingDataWrapper.getEfile().put(EfileConstants.OUTGOING, outgoingResponse);

        ClaimEfile claimEfile = new ClaimEfile();
        Objektreferenz collectionReference = (Objektreferenz) processingDataWrapper.getEfile().get(OperationId.READ_CASE_FILE_COLLECTIONS.name());
        claimEfile.setCollection(collectionReference.getObjaddress());
        DmsObjektResponse caseResponse = (DmsObjektResponse) processingDataWrapper.getEfile().get(EfileConstants.CASE_FILE);
        claimEfile.setCaseFile(caseResponse.getObjid());
        DmsObjektResponse fineResponse = (DmsObjektResponse) processingDataWrapper.getEfile().get(EfileConstants.FINE_FILE);
        claimEfile.setFine(fineResponse.getObjid());
        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.OUTGOING_ADDED_TO_FINE, MessageType.INFO, exchange);

        outgoingResponse.getGiobjecttype().forEach(document -> {
            if (document.getObjname().toLowerCase().endsWith(Constants.ANTRAG_EXTENSION.toLowerCase())) {
                claimEfile.setAntragDocument(document.getObjaddress());
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.CONTENT_OBJECT_ANTRAG_ADDED_TO_OUTGOING, MessageType.INFO, exchange);
            } else {
                claimEfile.setBescheidDocument(document.getObjaddress());
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.CONTENT_OBJECT_URBESCHEID_ADDED_TO_OUTGOING, MessageType.INFO, exchange);
            }
        });

        claimEfileRepository.save(claimEfile);
        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_OBJECTADDRESSES_SAVED, MessageType.INFO, exchange);
    }
}
