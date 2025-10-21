package de.muenchen.eh.kvue.claim.efile.operation.subjectdata;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.kvue.claim.efile.properties.FileProperties;
import de.muenchen.eh.kvue.claim.efile.properties.FineProperties;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.Claim;
import de.muenchen.eh.log.db.entity.MessageType;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class UpdateFineSubjectData extends UpdateSubjectData {

    private static final String OWI_NUMBER = "Ordnungswidrigkeiten-Nummer";

    private final FineProperties properties;
    private Optional<Map<String, String>> subjectProperties = Optional.empty();

    public UpdateFineSubjectData(LogServiceClaim logServiceClaim, Exchange exchange, ProducerTemplate efileConnector, OperationIdFactory operationIdFactory, FineProperties properties) {
        super(logServiceClaim, exchange, efileConnector, operationIdFactory);
        this.properties = properties;
    }

    @Override
    protected Map<String, String> subjectDataValuesBuilder() {

        Map<String, String> subjectDataValues = new HashMap<String, String>();

        Claim claim = subjectExchange.getMessage().getBody(ClaimProcessingContentWrapper.class).getClaim();
        subjectProperties = Optional.ofNullable(properties.getSubjectDataValues());

        if (subjectProperties.isPresent()) {

            for (Map.Entry<String, String> entry : properties.getSubjectDataValues().entrySet()) {

                if (entry.getValue().equals(OWI_NUMBER)) {
                    subjectDataValues.put(entry.getKey(), claim.getKassenzeichen());
                }
            };
        }

        return subjectDataValues;
    }

    @Override
    protected void logMessage() {
        if (subjectProperties.isPresent())
             logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.SUBJECT_OWI_DATA_SAVED, MessageType.INFO, subjectExchange);
        else
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.SUBJECT_DATA_SKIPPED, MessageType.WARN, subjectExchange);
    }


}
