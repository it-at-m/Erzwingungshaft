package de.muenchen.eh.claim.efile.operation.contentbuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.properties.FineProperties;
import de.muenchen.eh.common.OffsetDateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutgoingAnfrageDTOBuilder {

    private final FineProperties fineProperties;
    private final ClaimContentWrapper contentWrapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static OutgoingAnfrageDTOBuilder create(FineProperties fineProperties, ClaimContentWrapper contentWrapper) {
        return new OutgoingAnfrageDTOBuilder(fineProperties, contentWrapper);
    }

    public Map<String, String> buildAsMap() {
        return createContent();
    }

    public String buildAsJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(createContent());
    }

    private Map<String, String> createContent() {
        Map<String, String> params = new HashMap<>();

        DmsObjektResponse fineFileReference = (DmsObjektResponse) contentWrapper.getEfile().get(OperationId.CREATE_FINE.name());

        assert fineFileReference.getObjid() != null;
        params.put("referrednumber", fineFileReference.getObjid());
        params.put("filesubj", fineProperties.getFilesubj());
        params.put("objterms",
                (contentWrapper.getClaimImport().getGeschaeftspartnerId().concat(";").concat(contentWrapper.getClaimImport().getKassenzeichen())));
        params.put("accdef", fineProperties.getAccdef());
        params.put("doctemplate", fineProperties.getDoctemplate());
        params.put("outgoingdate", OffsetDateTimeFormatter.formatNow());
        //      params.put("subfiletype", fineProperties.getSubfiletype());
        params.put("incattachments", fineProperties.getIncattachments());
        params.put("shortname", fineProperties.getOutgoing());

        return params;
    }

}
