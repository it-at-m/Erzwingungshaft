package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.subjectdata.UpdateFineSubjectData;
import de.muenchen.eh.claim.efile.properties.FineProperties;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.log.StatusProcessingType;
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
    public void execute(Exchange exchange) {

        Exchange createCaseFileRequest = operationIdFactory.createExchange(OperationId.CREATE_FINE, exchange);
        Exchange createCaseFileResponse = efileConnector.send(createCaseFileRequest);
        if (createCaseFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }
        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
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
