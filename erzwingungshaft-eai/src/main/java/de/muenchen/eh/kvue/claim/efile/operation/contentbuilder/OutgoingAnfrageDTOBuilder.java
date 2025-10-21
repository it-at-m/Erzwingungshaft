package de.muenchen.eh.kvue.claim.efile.operation.contentbuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eh.common.OffsetDateTimeFormatter;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.kvue.claim.efile.properties.FineProperties;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class OutgoingAnfrageDTOBuilder {

    private final FineProperties fineProperties;
    private final ClaimProcessingContentWrapper contentWrapper;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public static OutgoingAnfrageDTOBuilder create(FineProperties fineProperties, ClaimProcessingContentWrapper contentWrapper) {
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
        params.put("referrednumber",fineFileReference.getObjid());
        params.put("filesubj", fineProperties.getFilesubj());
        params.put("objterms", (contentWrapper.getClaimImport().getGeschaeftspartnerId().concat(";").concat(contentWrapper.getClaimImport().getKassenzeichen())));
        params.put("accdef", fineProperties.getAccdef());
        params.put("doctemplate", fineProperties.getDoctemplate());
        params.put("outgoingdate", OffsetDateTimeFormatter.formatNow());
//      params.put("subfiletype", fineProperties.getSubfiletype());
        params.put("incattachments", fineProperties.getIncattachments());
        params.put("shortname", fineProperties.getOutgoing());

        return params;
    }

}
