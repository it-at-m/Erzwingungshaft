package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete;

import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.ClaimEfileRepository;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFine extends AbstractEAPLOperation {

    public AddFine(CompleteOperationIdFactory completeOperationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository) {
        super(completeOperationIdFactory, logServiceClaim, claimEfileRepository);
    }

    @Override
    public void execute(Exchange exchange) {

        Exchange createCaseFileRequest = completeOperationIdFactory.createExchange(OperationId.CREATE_FINE, exchange);
        Exchange createCaseFileResponse = efileConnector.send(createCaseFileRequest);
        if (createCaseFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }
        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        processingDataWrapper.getEfile().put(OperationId.CREATE_FINE.name(), createCaseFileResponse.getMessage().getBody());
        persistClaimEfileReference(exchange, OperationId.CREATE_FINE);
        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FINE_ADDED_TO_FILE, MessageType.INFO, exchange);
    }
}
