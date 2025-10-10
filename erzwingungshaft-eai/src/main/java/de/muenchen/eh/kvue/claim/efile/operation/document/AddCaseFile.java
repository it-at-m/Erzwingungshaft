package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.EfileConstants;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.MessageType;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddCaseFile extends EfileOperation {

    public AddCaseFile(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim) {
        super(operationIdFactory, logServiceClaim);
    }

    @Override
    public void execute(Exchange exchange) {

        Exchange createCaseFileRequest = operationIdFactory.createExchange(OperationId.CREATE_FILE, exchange);
        Exchange createCaseFileResponse = efileConnector.send(createCaseFileRequest);
        if (createCaseFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }
        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
        processingDataWrapper.getEfile().put(EfileConstants.CASE_FILE, createCaseFileResponse.getMessage().getBody());
        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.FILE_ADDED_TO_COLLECTION, MessageType.INFO, exchange);

    }
}
