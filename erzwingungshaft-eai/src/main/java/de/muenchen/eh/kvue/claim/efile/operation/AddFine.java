package de.muenchen.eh.kvue.claim.efile.operation;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.subjectdata.UpdateFineSubjectData;
import de.muenchen.eh.kvue.claim.efile.properties.FineProperties;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFine extends EfileOperation {

    private final FineProperties fineProperties;

    public AddFine(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository,
            FineProperties fineProperties) {
        super(operationIdFactory, logServiceClaim, claimEfileRepository);
        this.fineProperties = fineProperties;
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
        logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FINE_ADDED_TO_CASE_FILE, MessageType.INFO, exchange);

        UpdateFineSubjectData updateSubjectData = new UpdateFineSubjectData(super.logServiceClaim, exchange, super.efileConnector, super.operationIdFactory,
                fineProperties);
        Exchange responseSubjectUpdate = updateSubjectData.execute();

        if (responseSubjectUpdate.isRouteStop()) {
            exchange.setRouteStop(true);
        }

    }
}
