package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.subjectdata.UpdateFileSubjectData;
import de.muenchen.eh.claim.efile.properties.FileProperties;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimDataRepository;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFile extends EfileOperation {

    private final ClaimDataRepository claimDataRepository;
    private final FileProperties fileProperties;

    public AddFile(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository,
            ClaimDataRepository claimDataRepository, FileProperties fileProperties) {
        super(operationIdFactory, logServiceClaim, claimEfileRepository);
        this.claimDataRepository = claimDataRepository;
        this.fileProperties = fileProperties;
    }

    @Override
    public void execute(Exchange exchange) {

        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        Optional<ClaimEfile> claimEfile = Optional.ofNullable(exchange.getIn().getBody(ClaimContentWrapper.class).getClaimEfile());

        if (claimEfile.isPresent() && claimEfile.get().getFile() != null) {
            processingDataWrapper.setClaimEfile(claimEfile.get());
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ALREADY_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);
        } else {
            Exchange createFileRequest = operationIdFactory.createExchange(OperationId.CREATE_FILE, exchange);
            Exchange createFileResponse = efileConnector.send(createFileRequest);

            if (createFileResponse.isRouteStop()) {
                exchange.setRouteStop(true);
                return;
            }
            processingDataWrapper.getEfile().put(OperationId.CREATE_FILE.name(), createFileResponse.getMessage().getBody());
            createUpdateClaimEfile(exchange, OperationId.CREATE_FILE);
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ADDED_TO_COLLECTION, MessageType.INFO, exchange);

            UpdateFileSubjectData updateSubjectData = new UpdateFileSubjectData(super.logServiceClaim, exchange, super.efileConnector, super.operationIdFactory,
                    fileProperties, claimDataRepository);
            Exchange responseSubjectUpdate = updateSubjectData.execute();

            if (responseSubjectUpdate.isRouteStop()) {
                exchange.setRouteStop(true);
            }

        }
    }
}
