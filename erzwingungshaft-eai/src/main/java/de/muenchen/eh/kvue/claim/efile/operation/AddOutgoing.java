package de.muenchen.eh.kvue.claim.efile.operation;

import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddOutgoing extends EfileOperation {

    @Produce(value = EfileRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate efileConnector;

    public AddOutgoing(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim, ClaimEfileRepository claimEfileRepository) {
        super(operationIdFactory, logServiceClaim, claimEfileRepository);
    }

    @Override
    public void execute(Exchange exchange) {

        Exchange createOutgoingRequest = operationIdFactory.createExchange(OperationId.CREATE_OUTGOING, exchange);
        Exchange createOutgoingResponse = efileConnector.send(createOutgoingRequest);
        if (createOutgoingResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }
        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
        CreateOutgoingAntwortDTO outgoingResponse = createOutgoingResponse.getMessage().getBody(CreateOutgoingAntwortDTO.class);
        processingDataWrapper.getEfile().put(OperationId.CREATE_OUTGOING.name(), outgoingResponse);
        Optional<ClaimEfile> saved = Optional.ofNullable(createUpdateClaimEfile(exchange, OperationId.CREATE_OUTGOING));

        saved.ifPresent(claimEfile -> {
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_OBJECTADDRESSES_SAVED, MessageType.INFO, exchange);
            if (claimEfile.getOutgoing() != null)
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_OUTGOING_ADDED_TO_FINE, MessageType.INFO, exchange);
            if (claimEfile.getAntragDocument() != null)
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_CONTENT_OBJECT_ANTRAG_ADDED_TO_OUTGOING, MessageType.INFO, exchange);
            if (claimEfile.getBescheidDocument() != null)
                logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_CONTENT_OBJECT_URBESCHEID_ADDED_TO_OUTGOING, MessageType.INFO, exchange);
        });

    }
}
