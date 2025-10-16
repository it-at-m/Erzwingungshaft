package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFine extends EfileOperation {

    public AddFine(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository) {
        super(operationIdFactory, logServiceClaim, claimEfileRepository);
    }

    @Override
    protected void execute(Exchange exchange) {

        Exchange createCaseFileRequest = operationIdFactory.createExchange(OperationId.CREATE_FINE, exchange);
        Exchange createCaseFileResponse = efileConnector.send(createCaseFileRequest);
        if (createCaseFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }
        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
        processingDataWrapper.getEfile().put(OperationId.CREATE_FINE.name(), createCaseFileResponse.getMessage().getBody());
        createUpdateClaimEfile(exchange, OperationId.CREATE_FINE);
        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.FINE_ADDED_TO_CASE_FILE, MessageType.INFO, exchange);
    }
}
