package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.db.LogServiceClaim;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CheckCaseFiles extends EfileOperation {

    private Map<String, ReadApentryAntwortDTO> apentryCache = new HashMap<>();

    public CheckCaseFiles(OperationIdFactory operationIdFactory, LogServiceClaim logServiceClaim) {
        super(operationIdFactory, logServiceClaim);
    }

    @Override
    protected void execute(Exchange exchange) {

        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);
        Objektreferenz objektreferenz = (Objektreferenz) processingDataWrapper.getEakte().get(OperationId.READ_APENTRY_COLLECTION.name());
        if (! apentryCache.containsKey(objektreferenz.getObjaddress())) {
            Exchange readApentryRequest = operationIdFactory.createExchange(OperationId.READ_APENTRY_CASE_FILES, exchange);
            Exchange eakteApentryResponse = eakteConnector.send(readApentryRequest);
            if (eakteApentryResponse.isRouteStop()) {
                exchange.setRouteStop(true);
                return;
            }
            apentryCache.put(objektreferenz.getObjaddress(), eakteApentryResponse.getMessage().getBody(ReadApentryAntwortDTO.class));
        }
        processingDataWrapper.getEakte().put(OperationId.READ_APENTRY_CASE_FILES.name(), apentryCache.get(objektreferenz.getObjaddress()));
    }

    public void clearApentryCache() {
        apentryCache.clear();
    }
}
