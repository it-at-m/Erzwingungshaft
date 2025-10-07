package de.muenchen.eh.kvue.claim.eakte.operation.document;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.eakte.EakteRouteBuilder;
import de.muenchen.eh.kvue.claim.eakte.operation.OperationId;
import de.muenchen.eh.kvue.claim.eakte.operation.OperationIdFactory;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class FindCollection implements EakteOperation {

    @Produce(value= EakteRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    private final OperationIdFactory operationIdFactory;

    private final LogServiceClaim logServiceClaim;

    private Optional<ReadApentryAntwortDTO> apentryCache = Optional.empty();

    @Override
    public void execute(Exchange exchange) {
        findGeschaeftsparterIdEinzelaktenCollection(exchange);
    }

    private void findGeschaeftsparterIdEinzelaktenCollection(Exchange exchange) {

        ClaimProcessingContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimProcessingContentWrapper.class);

        if (apentryCache.isEmpty()) {
            Exchange readApentryRequest = operationIdFactory.createExchange(OperationId.READ_APENTRY, exchange.getProperty(Constants.CLAIM));
            Exchange eakteApentryResponse = eakteConnector.send(readApentryRequest);
            apentryCache = Optional.ofNullable(eakteApentryResponse.getMessage().getBody(ReadApentryAntwortDTO.class));
            if (log.isDebugEnabled())
                apentryCache.ifPresent(a -> a.getGiobjecttype().forEach(o -> {log.debug(o.toString());}));
        }

        apentryCache.ifPresent(apentry -> {
             List<Objektreferenz> einzelakten = gpIdFilter(apentry.getGiobjecttype(), Long.valueOf(processingDataWrapper.getClaim().getGeschaeftspartnerId()));
             if (einzelakten.isEmpty()) {
                    logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.GESCHAEFTSPARTNERID_EINZELKAKTE_NOT_FOUND, MessageType.ERROR, exchange);
                    exchange.setRouteStop(true);
                    return;
             }

            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.GESCHAEFTSPARTNERID_EINZELKAKTE_FOUND, MessageType.INFO, exchange);

        });


    }

    private List<Objektreferenz> gpIdFilter(List<Objektreferenz> objektList, long gpid) {
        return objektList.stream()
                .filter(obj -> {
                    String objname = obj.getObjname();
                    String[] parts = objname.split("/");
                    if (parts.length != 3) {
                        return false;
                    }
                    String rangePart = parts[2];
                    String[] range = rangePart.split("-");
                    if (range.length != 2) {
                        return false;
                    }
                    try {
                        long gpidVon = Long.parseLong(range[0]);
                        long gpidBis = Long.parseLong(range[1]);
                        return gpid >= gpidVon && gpid <= gpidBis;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public void clearApentryCache() {
        apentryCache = Optional.empty();
    }

}
