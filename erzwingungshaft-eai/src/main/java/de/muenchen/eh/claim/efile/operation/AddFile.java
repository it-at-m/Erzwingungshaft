package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.subjectdata.UpdateFileSubjectData;
import de.muenchen.eh.claim.efile.properties.FileProperties;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimDataRepository;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.log.StatusProcessingType;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFile extends EfileOperation {

    private final ClaimDataRepository claimDataRepository;
    private final FileProperties fileProperties;
    private final UpdateFileSubjectData updateFileSubjectData;

    public AddFile(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository,
            ClaimDataRepository claimDataRepository, FileProperties fileProperties, UpdateFileSubjectData updateFileSubjectData) {

        super(operationIdFactory, logServiceClaim, claimEfileRepository);
        this.claimDataRepository = claimDataRepository;
        this.fileProperties = fileProperties;
        this.updateFileSubjectData = updateFileSubjectData;
    }

    @Override
    public void execute(Exchange exchange) {

        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        Optional<ClaimEfile> claimEfile = Optional.ofNullable(exchange.getIn().getBody(ClaimContentWrapper.class).getClaimEfile());

        if (claimEfile.isPresent() && claimEfile.get().getFile() != null) {
            processingDataWrapper.setClaimEfile(claimEfile.get());
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ALREADY_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);

            Exchange responseSubjectUpdate = updateSubjectData(exchange);

            if (responseSubjectUpdate.isRouteStop()) {
                exchange.setRouteStop(true);
            }

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

            Exchange responseSubjectUpdate = updateSubjectData(exchange);

            if (responseSubjectUpdate.isRouteStop()) {
                exchange.setRouteStop(true);
            }

        }
    }

    private Exchange updateSubjectData(Exchange exchange) {
        return this.updateFileSubjectData.execute(exchange, OperationId.UPDATE_SUBJECT_DATA_FILE);
    }
}
