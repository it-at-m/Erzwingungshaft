package de.muenchen.eh.claim.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateSubjectAreaUnitAnfrageDTO;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.GpidRangeGenerator;
import de.muenchen.eh.claim.efile.properties.FileProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubjectAreaUnitRequestDTOBuilder {

    private final ClaimContentWrapper contentWrapper;
    private final FileProperties fileProperties;

    public static SubjectAreaUnitRequestDTOBuilder create(ClaimContentWrapper contentWrapper, FileProperties fileProperties) {
        return new SubjectAreaUnitRequestDTOBuilder(contentWrapper, fileProperties);
    }

    public CreateSubjectAreaUnitAnfrageDTO build() {
        return createCaseFileDTO();
    }

    private CreateSubjectAreaUnitAnfrageDTO createCaseFileDTO() {

        String[] splitParts = GpidRangeGenerator.counterAndRangeSplitted(Long.parseLong(contentWrapper.getClaimImport().getGeschaeftspartnerId()));

        CreateSubjectAreaUnitAnfrageDTO createSubjectAreaUnitAnfrageDTO = new CreateSubjectAreaUnitAnfrageDTO();

        createSubjectAreaUnitAnfrageDTO.setObjaddress(fileProperties.getObjaddress());
        createSubjectAreaUnitAnfrageDTO.setBasenr(fileProperties.getBasenr() + splitParts[0]);
        createSubjectAreaUnitAnfrageDTO.setShortterm(splitParts[1]);

        return createSubjectAreaUnitAnfrageDTO;
    }

}
