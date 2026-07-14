package de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateProcedureDTO;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.integration.efile.properties.FineProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcedureDTOBuilder {

    private final FineProperties fineProperties;
    private final ClaimContentWrapper contentWrapper;

    public static ProcedureDTOBuilder create(FineProperties fineProperties, ClaimContentWrapper contentWrapper) {
        return new ProcedureDTOBuilder(fineProperties, contentWrapper);
    }

    public CreateProcedureDTO build() {
        return createProcedureDTO();
    }

    private CreateProcedureDTO createProcedureDTO() {

        CreateProcedureDTO createProcedureDTO = new CreateProcedureDTO();

        createProcedureDTO.setShortname(fineProperties.getShortname());
        createProcedureDTO.setReferrednumber(contentWrapper.getClaimEfile().getFile());
        createProcedureDTO.setFilesubj(contentWrapper.getClaimImport().getKassenzeichen());
        createProcedureDTO.setDefinition(fineProperties.getEhVorgangDefinition());

        return createProcedureDTO;
    }

}




