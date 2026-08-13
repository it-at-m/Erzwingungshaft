package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.identifier;

import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eh.domain.identifier.IdentifierContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.integration.efile.EfileRouteBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.AbstractEAPLExecute;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component()
@RequiredArgsConstructor
public class IdentifierFine extends AbstractEAPLExecute {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    private final ProducerTemplate efileConnector;

    private final IdentifierOperationIdFactory identifierOperationIdFactory;

    @Override
    protected void execute(Exchange exchange) {

        Exchange createCaseFileRequest = identifierOperationIdFactory.createExchange(OperationId.CREATE_FINE, exchange);
        Exchange createCaseFileResponse = efileConnector.send(createCaseFileRequest);
        if (createCaseFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return;
        }

        IdentifierContentWrapper identifierContentWrapper = exchange.getMessage().getBody(IdentifierContentWrapper.class);

        DmsObjektResponse fineReference = createCaseFileResponse.getMessage().getBody(DmsObjektResponse.class);
        identifierContentWrapper.getEfile().put(OperationId.CREATE_FINE.name(), fineReference);
        identifierContentWrapper.getEfileIdentifier().setFineCooAddress(fineReference.getObjid());
        identifierContentWrapper.getEfileIdentifier().setFineNameCooAddress(fineReference.getObjname());

        identifierContentWrapper.getEfileIdentifier().setMessageType(MessageType.INFO);
        identifierContentWrapper.getEfileIdentifier().setMessage(StatusProcessingType.EFILE_FINE_ADDED_TO_FILE.getDescriptor());
    }
}
