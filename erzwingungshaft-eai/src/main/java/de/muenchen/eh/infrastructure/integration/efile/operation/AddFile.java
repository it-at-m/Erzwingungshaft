package de.muenchen.eh.infrastructure.integration.efile.operation;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.SearchFileResponseDTO;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.integration.efile.operation.userformdata.UpdateFileUserFormData;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.ClaimDataRepository;
import de.muenchen.eh.infrastructure.db.repository.ClaimEfileRepository;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class AddFile extends EfileOperation {

    private final ClaimDataRepository claimDataRepository;
    private final UpdateFileUserFormData updateFileUserFormData;

    public AddFile(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository,
            ClaimDataRepository claimDataRepository, UpdateFileUserFormData updateFileUserFormData) {

        super(operationIdFactory, logServiceClaim, claimEfileRepository);
        this.claimDataRepository = claimDataRepository;
        this.updateFileUserFormData = updateFileUserFormData;
    }

    @Override
    public void execute(Exchange exchange) {

        ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);
        Optional<ClaimEfile> claimEfile = Optional.ofNullable(processingDataWrapper.getClaimEfile());

        // Database contains no efile file
        if (claimEfile.isPresent() && claimEfile.get().getFile() != null) {

            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ALREADY_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);

            Exchange responseUpdate = updateFileUserFormData(exchange);

            if (responseUpdate.isRouteStop()) {
                exchange.setRouteStop(true);
            }

        } else {

            Optional<List<Objektreferenz>> eFileFilesWithGpid = checkIfEfileFileWithGpidExists(exchange);

            if (exchange.isRouteStop())
                return;

            eFileFilesWithGpid.ifPresentOrElse(list -> {

                // Efile for gpId exists
                createUpdateClaimEfile(exchange, OperationId.SEARCH_FILE);
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ALREADY_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);

            }, () -> {

                // Create new efile file if not exists
                Exchange createFileRequest = operationIdFactory.createExchange(OperationId.CREATE_FILE, exchange);
                Exchange createFileResponse = efileConnector.send(createFileRequest);

                if (createFileResponse.isRouteStop()) {
                    exchange.setRouteStop(true);
                    return;
                }
                processingDataWrapper.getEfile().put(OperationId.CREATE_FILE.name(), createFileResponse.getMessage().getBody());
                createUpdateClaimEfile(exchange, OperationId.CREATE_FILE);
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

    private Optional<List<Objektreferenz>> checkIfEfileFileWithGpidExists(Exchange exchange) {

        // Check if efile apentry contains file with gpid
        Exchange createSearchFileRequest = operationIdFactory.createExchange(OperationId.SEARCH_FILE, exchange);
        Exchange createSearchFileResponse = efileConnector.send(createSearchFileRequest);

        if (createSearchFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return Optional.empty();
        }

        ClaimContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

        var gpid = dataWrapper.getClaimImport().getGeschaeftspartnerId();

        SearchFileResponseDTO files = createSearchFileResponse.getMessage().getBody(SearchFileResponseDTO.class);

        List<Objektreferenz> filteredFiles = files.getGiobjecttype().stream()
                .filter(objref -> objref.getObjname().contains("-" + gpid + "-"))
                .sorted(Comparator.comparingInt(this::extractTrailingNumber))
                .toList();

        dataWrapper.getEfile().put(OperationId.SEARCH_FILE.name(), filteredFiles);

        return filteredFiles.isEmpty() ? Optional.empty() : Optional.of(filteredFiles);
    }

    private int extractTrailingNumber(Objektreferenz ref) {
        String s = ref.getObjname();
        return Integer.parseInt(s.substring(s.lastIndexOf('-') + 1));
    }

}




