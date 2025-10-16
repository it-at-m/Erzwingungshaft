package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.CreateProcedureDTO;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.properties.ShortnameProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcedureDTOBuilder {

    private final ShortnameProperties shortnameProperties;
    private final ClaimProcessingContentWrapper contentWrapper;
    private final CreateProcedureDTO createProcedureDTO = new CreateProcedureDTO();

    public static ProcedureDTOBuilder create(ShortnameProperties shortnameProperties, ClaimProcessingContentWrapper contentWrapper) {
        return new ProcedureDTOBuilder(shortnameProperties, contentWrapper);
    }

    public CreateProcedureDTO build() {
        return createProcedureDTO();
    }

    private CreateProcedureDTO createProcedureDTO() {

        createProcedureDTO.setShortname(shortnameProperties.getFine());
        createProcedureDTO.setReferrednumber(contentWrapper.getClaimEfile().getFile());
        createProcedureDTO.setFilesubj(contentWrapper.getClaim().getKassenzeichen());

        return createProcedureDTO;
    }

}
