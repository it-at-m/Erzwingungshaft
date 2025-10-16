package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddCaseFile extends EfileOperation {

    public AddCaseFile(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository) {
        super(operationIdFactory, logServiceClaim, claimEfileRepository);
    }

    @Override
    public void execute(Exchange exchange) {

       ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
       Optional<ClaimEfile> claimEfile = Optional.ofNullable(exchange.getIn().getBody(ClaimProcessingContentWrapper.class).getClaimEfile());

       if (claimEfile.isPresent() && claimEfile.get().getFile() != null) {
           processingDataWrapper.setClaimEfile(claimEfile.get());
           logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.FILE_ALREADY_EXISTS_IN_COLLECTION, MessageType.INFO, exchange);
        } else {
           Exchange createCaseFileRequest = operationIdFactory.createExchange(OperationId.CREATE_FILE, exchange);
           Exchange createCaseFileResponse = efileConnector.send(createCaseFileRequest);
           if (createCaseFileResponse.isRouteStop()) {
               exchange.setRouteStop(true);
               return;
           }
           processingDataWrapper.getEfile().put(OperationId.CREATE_FILE.name(), createCaseFileResponse.getMessage().getBody());
           createUpdateClaimEfile(exchange, OperationId.CREATE_FILE);
           logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.FILE_ADDED_TO_COLLECTION, MessageType.INFO, exchange);
       }
    }
}
