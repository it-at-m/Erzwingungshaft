package de.muenchen.eh.kvue.claim.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateProcedureDTO;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.properties.FineProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcedureDTOBuilder {

    private final FineProperties fineProperties;
    private final ClaimProcessingContentWrapper contentWrapper;
    private final CreateProcedureDTO createProcedureDTO = new CreateProcedureDTO();

    public static ProcedureDTOBuilder create(FineProperties fineProperties, ClaimProcessingContentWrapper contentWrapper) {
        return new ProcedureDTOBuilder(fineProperties, contentWrapper);
    }

    public CreateProcedureDTO build() {
        return createProcedureDTO();
    }

    private CreateProcedureDTO createProcedureDTO() {

        createProcedureDTO.setShortname(fineProperties.getShortname());
        createProcedureDTO.setReferrednumber(contentWrapper.getClaimEfile().getFile());
        createProcedureDTO.setFilesubj(contentWrapper.getClaimImport().getKassenzeichen());

        return createProcedureDTO;
    }

}
