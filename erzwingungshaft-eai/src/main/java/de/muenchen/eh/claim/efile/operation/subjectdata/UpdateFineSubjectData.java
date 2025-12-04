package de.muenchen.eh.claim.efile.operation.subjectdata;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.claim.efile.properties.FineProperties;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.log.StatusProcessingType;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class UpdateFineSubjectData extends UpdateSubjectData {

    private static final String OWI_NUMBER = "Ordnungswidrigkeiten-Nummer";

    private final FineProperties properties;

    @Nullable private Map<String, String> subjectProperties;

    public UpdateFineSubjectData(LogServiceClaim logServiceClaim, OperationIdFactory operationIdFactory,
            FineProperties properties) {
        super(logServiceClaim, operationIdFactory);
        this.properties = properties;
    }

    @Override
    protected Map<String, String> subjectDataValuesBuilder(Exchange exchange) {

        this.subjectExchange = exchange;

        Map<String, String> subjectDataValues = new HashMap<>();

        ClaimImport claim = subjectExchange.getMessage().getBody(ClaimContentWrapper.class).getClaimImport();
        subjectProperties = properties.getSubjectDataValues();

        if (subjectProperties != null) {

            for (Map.Entry<String, String> entry : properties.getSubjectDataValues().entrySet()) {

                if (entry.getValue().equals(OWI_NUMBER)) {
                    subjectDataValues.put(entry.getKey(), claim.getKassenzeichen());
                }
            }
        }

        return subjectDataValues;
    }

    @Override
    protected void logMessage() {
        if (subjectProperties != null)
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_SUBJECT_OWI_DATA_SAVED, MessageType.INFO, subjectExchange);
        else
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_SUBJECT_DATA_SKIPPED, MessageType.WARN, subjectExchange);
    }

}
