package de.muenchen.eh.kvue.claim.efile.operation.subjectdata;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.kvue.claim.efile.properties.FileProperties;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceClaim;
import de.muenchen.eh.log.db.entity.Claim;
import de.muenchen.eh.log.db.entity.ClaimData;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimDataRepository;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class UpdateFileSubjectData extends UpdateSubjectData {

    private static final String NAME_GESCHAEFTSPARTNER = "Name-des-Geschaeftspartner";
    private static final String FIRST_NAME_GESCHAEFTSPARTNER = "Vorname-des-Geschaeftspartner";
    private static final String BIRTHDATE_GESCHAEFTSPARTNER = "Geburtstag-des-Geschaeftspartner";

    private final FileProperties properties;
    private final ClaimDataRepository claimDataRepository;
    @Nullable
    private Map<String, String> subjectProperties ;

    public UpdateFileSubjectData(LogServiceClaim logServiceClaim, Exchange exchange, ProducerTemplate efileConnector, OperationIdFactory operationIdFactory, FileProperties properties, ClaimDataRepository claimDataRepository) {
        super(logServiceClaim, exchange, efileConnector, operationIdFactory);
        this.properties = properties;
        this.claimDataRepository = claimDataRepository;
    }

    @Override
    protected Map<String, String> subjectDataValuesBuilder() {

        Map<String, String> subjectDataValues = new HashMap<>();

        Claim claim = subjectExchange.getMessage().getBody(ClaimProcessingContentWrapper.class).getClaim();
        ClaimData claimData = claimDataRepository.findByClaimId(claim.getId());
        subjectProperties = properties.getSubjectDataValues();

        if (subjectProperties != null) {
            for (Map.Entry<String, String> entry : properties.getSubjectDataValues().entrySet()) {

                if (entry.getValue().equals(NAME_GESCHAEFTSPARTNER)) {
                    subjectDataValues.put(entry.getKey(), claimData.getEhp1name());
                } else if (entry.getValue().equals(FIRST_NAME_GESCHAEFTSPARTNER)) {
                    subjectDataValues.put(entry.getKey(), claimData.getEhp1vorname());
                } else if (entry.getValue().equals(BIRTHDATE_GESCHAEFTSPARTNER)) {
                    subjectDataValues.put(entry.getKey(), claimData.getEhp1gebdat());
                }
            }
        }
        return subjectDataValues;
    }

    @Override
    protected void logMessage() {
        if (subjectProperties != null)
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.SUBJECT_FILE_DATA_SAVED, MessageType.INFO, subjectExchange);
        else
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.SUBJECT_FILE_DATA_SAVED, MessageType.WARN, subjectExchange);
    }


}
