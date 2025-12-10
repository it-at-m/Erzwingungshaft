package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.SearchFileResponseDTO;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.subjectdata.UpdateFileSubjectData;
import de.muenchen.eh.claim.efile.properties.FileProperties;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimDataRepository;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.log.StatusProcessingType;
import java.util.Comparator;
import java.util.List;
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

        // Database contains no efile file
        if (claimEfile.isPresent() && claimEfile.get().getFile() != null) {
            processingDataWrapper.setClaimEfile(claimEfile.get());
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_FILE_ALREADY_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);

            Exchange responseSubjectUpdate = updateSubjectData(exchange);

            if (responseSubjectUpdate.isRouteStop()) {
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

                Exchange responseSubjectUpdate = updateSubjectData(exchange);

                if (responseSubjectUpdate.isRouteStop()) {
                    exchange.setRouteStop(true);
                }
            });
        }
    }

    private Exchange updateSubjectData(Exchange exchange) {
        return this.updateFileSubjectData.execute(exchange, OperationId.UPDATE_SUBJECT_DATA_FILE);
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
