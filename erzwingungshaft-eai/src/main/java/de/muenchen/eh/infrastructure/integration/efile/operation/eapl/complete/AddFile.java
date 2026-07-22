package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.ClaimDataRepository;
import de.muenchen.eh.infrastructure.db.repository.ClaimEfileRepository;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.GeschaftspartnerIdEfile;
import de.muenchen.eh.infrastructure.integration.efile.operation.userformdata.UpdateFileUserFormData;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.List;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFile extends AbstractEAPLOperation {

    private final ClaimDataRepository claimDataRepository;
    private final UpdateFileUserFormData updateFileUserFormData;
    private final GeschaftspartnerIdEfile geschaftspartnerIdEfile;

    public AddFile(CompleteOperationIdFactory completeOperationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository,
            ClaimDataRepository claimDataRepository, UpdateFileUserFormData updateFileUserFormData, GeschaftspartnerIdEfile geschaftspartnerIdEfile) {

        super(completeOperationIdFactory, logServiceClaim, claimEfileRepository);
        this.claimDataRepository = claimDataRepository;
        this.updateFileUserFormData = updateFileUserFormData;
        this.geschaftspartnerIdEfile = geschaftspartnerIdEfile;
    }

    @Override
    public void execute(Exchange exchange) {

        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        Optional<ClaimEfile> claimEfile = Optional.ofNullable(processingDataWrapper.getClaimEfile());

        // Database contains no efile file
        if (claimEfile.isPresent() && claimEfile.get().getFile() != null) {

            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);

            Exchange responseUpdate = updateFileUserFormData(exchange);

            if (responseUpdate.isRouteStop()) {
                exchange.setRouteStop(true);
            }

        } else {

            Exchange createSearchFileRequest = completeOperationIdFactory.createExchange(OperationId.SEARCH_FILE, exchange);
            String geschaeftspartnerId = exchange.getMessage().getBody(ClaimContentWrapper.class).getClaimImport().getGeschaeftspartnerId();
            Optional<List<Objektreferenz>> eFileFilesWithGpid = geschaftspartnerIdEfile.checkIfEfileFileWithGpidExists(exchange, createSearchFileRequest,
                    geschaeftspartnerId);

            if (exchange.isRouteStop())
                return;

            eFileFilesWithGpid.ifPresentOrElse(list -> {

                // Efile for gpId exists
                persistClaimEfileReference(exchange, OperationId.SEARCH_FILE);
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);

            }, () -> {

                // Create new efile file if not exists
                Exchange createFileRequest = completeOperationIdFactory.createExchange(OperationId.CREATE_FILE, exchange);
                Exchange createFileResponse = efileConnector.send(createFileRequest);

                if (createFileResponse.isRouteStop()) {
                    exchange.setRouteStop(true);
                    return;
                }
                processingDataWrapper.getEfile().put(OperationId.CREATE_FILE.name(), createFileResponse.getMessage().getBody());
                persistClaimEfileReference(exchange, OperationId.CREATE_FILE);
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ADDED_TO_COLLECTION, MessageType.INFO, exchange);

                Exchange responseUpdate = updateFileUserFormData(exchange);

                if (responseUpdate.isRouteStop()) {
                    exchange.setRouteStop(true);
                }
            });
        }
    }

    private Exchange updateFileUserFormData(Exchange exchange) {
        return this.updateFileUserFormData.execute(exchange, OperationId.UPDATE_USER_FORMS_DATA);
    }

}
