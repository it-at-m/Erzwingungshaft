package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.identifier;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.domain.identifier.IdentifierContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.AbstractEAPLExecute;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.GeschaftspartnerIdEfile;
import de.muenchen.eh.infrastructure.log.LogServiceIdentifier;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentifierFile extends AbstractEAPLExecute {

    private final GeschaftspartnerIdEfile geschaftspartnerIdEfile;
    private final LogServiceIdentifier logServiceIdentifier;
    private final IdentifierOperationIdFactory identifierOperationIdFactory;

    @Override
    protected void execute(Exchange exchange) {

        Exchange createSearchFileRequest = identifierOperationIdFactory.createExchange(OperationId.SEARCH_FILE, exchange);
        String geschaeftspartnerId = exchange.getMessage().getBody(IdentifierContentWrapper.class).getEfileIdentifier().getGeschaeftspartnerId();
        Optional<List<Objektreferenz>> eFileFilesWithGpid = geschaftspartnerIdEfile.checkIfEfileFileWithGpidExists(exchange, createSearchFileRequest,
                geschaeftspartnerId);

        if (exchange.isRouteStop())
            return;

        IdentifierContentWrapper identifierContentWrapper = exchange.getMessage().getBody(IdentifierContentWrapper.class);

        eFileFilesWithGpid.ifPresentOrElse(list -> {

            if (list.size() > 1) {
                logServiceIdentifier.writeError(StatusProcessingType.EFILE_FILE_AMBIGUOUS, exchange);
                exchange.setRouteStop(true);

            } else {
                Objektreferenz efiles = list.getFirst();
                identifierContentWrapper.getEfile().put(OperationId.SEARCH_FILE.name(), efiles);
                identifierContentWrapper.getEfileIdentifier().setFileCooAddress(efiles.getObjaddress());

                identifierContentWrapper.getEfileIdentifier().setMessageType(MessageType.INFO);
                identifierContentWrapper.getEfileIdentifier().setMessage(StatusProcessingType.EFILE_FILE_EXISTS_IN_COLLECTION.getDescriptor());
            }

        }, () -> {

            logServiceIdentifier.writeError(StatusProcessingType.EFILE_FILE_NOT_FOUND, exchange);
            exchange.setRouteStop(true);

        });

    }
}
