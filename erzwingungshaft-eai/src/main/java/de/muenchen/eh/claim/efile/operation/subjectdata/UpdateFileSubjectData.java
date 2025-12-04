package de.muenchen.eh.claim.efile.operation.subjectdata;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.claim.efile.properties.FileProperties;
import de.muenchen.eh.db.entity.Claim;
import de.muenchen.eh.db.entity.ClaimData;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimDataRepository;
import de.muenchen.eh.log.LogServiceClaim;
import de.muenchen.eh.log.StatusProcessingType;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class UpdateFileSubjectData extends UpdateSubjectData {

    private static final String NAME_GESCHAEFTSPARTNER = "BusinessDataGPSurname";
    private static final String FIRST_NAME_GESCHAEFTSPARTNER = "BusinessDataGPFirstname";
    private static final String BIRTHDATE_GESCHAEFTSPARTNER = "BusinessDataGPBirthDate";

    private final FileProperties properties;
    private final ClaimDataRepository claimDataRepository;
    @Nullable private Map<String, String> subjectProperties;

    public UpdateFileSubjectData(LogServiceClaim logServiceClaim, OperationIdFactory operationIdFactory,
            FileProperties properties, ClaimDataRepository claimDataRepository) {
        super(logServiceClaim, operationIdFactory);
        this.properties = properties;
        this.claimDataRepository = claimDataRepository;
    }

    @Override
    protected Map<String, String> subjectDataValuesBuilder(Exchange exchange) {

        this.subjectExchange = exchange;

        Map<String, String> subjectDataValues = new HashMap<>();

        Claim claim = subjectExchange.getMessage().getBody(ClaimContentWrapper.class).getClaim();
        ClaimData claimData = claimDataRepository.findByClaimId(claim.getId());
        subjectProperties = properties.getSubjectDataValues();

        if (subjectProperties != null) {
            for (Map.Entry<String, String> entry : properties.getSubjectDataValues().entrySet()) {

                if (entry.getValue().equals(NAME_GESCHAEFTSPARTNER)) {
                    subjectDataValues.put(entry.getValue(), claimData.getEhp1name());
                } else if (entry.getValue().equals(FIRST_NAME_GESCHAEFTSPARTNER)) {
                    subjectDataValues.put(entry.getValue(), claimData.getEhp1vorname());
                } else if (entry.getValue().equals(BIRTHDATE_GESCHAEFTSPARTNER)) {
                    subjectDataValues.put(entry.getValue(), claimData.getEhp1gebdat());
                }
            }
        }
        return subjectDataValues;
    }

    @Override
    protected void logMessage() {
        if (subjectProperties != null)
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_SUBJECT_FILE_DATA_SAVED, MessageType.INFO, subjectExchange);
        else
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.EFILE_SUBJECT_DATA_SKIPPED, MessageType.WARN, subjectExchange);
    }

}
