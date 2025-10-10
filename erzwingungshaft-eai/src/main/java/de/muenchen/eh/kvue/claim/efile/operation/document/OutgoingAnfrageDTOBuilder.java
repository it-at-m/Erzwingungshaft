package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.CreateOutgoingAnfrageDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.EfileConstants;
import de.muenchen.eh.kvue.claim.efile.properties.ShortnameProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutgoingAnfrageDTOBuilder {

    private final ShortnameProperties shortnameProperties;
    private final ClaimProcessingContentWrapper contentWrapper;
    private CreateOutgoingAnfrageDTO requestDTO = new CreateOutgoingAnfrageDTO();

    public static OutgoingAnfrageDTOBuilder create(ShortnameProperties shortnameProperties, ClaimProcessingContentWrapper contentWrapper) {
        return new OutgoingAnfrageDTOBuilder(shortnameProperties, contentWrapper);
    }

    public CreateOutgoingAnfrageDTO build() {
        return createRequestDTO();
    }

    private CreateOutgoingAnfrageDTO createRequestDTO() {

        DmsObjektResponse fineFileReference = (DmsObjektResponse) contentWrapper.getEfile().get(EfileConstants.FINE_FILE);

        assert fineFileReference.getObjid() != null;
        requestDTO.setReferrednumber(fineFileReference.getObjid());
        requestDTO.setShortname(shortnameProperties.getOutgoing());

        return requestDTO;
    }

}
