package de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateSubjectAreaUnitAnfrageDTO;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.integration.efile.GpidRangeGenerator;
import de.muenchen.eh.infrastructure.integration.efile.properties.FileProperties;
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

        String geschaeftspartnerId = contentWrapper.getClaimImport().getGeschaeftspartnerId();
        long parsedGeschaeftspartnerId;
        try {
            parsedGeschaeftspartnerId = Long.parseLong(geschaeftspartnerId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid geschaeftspartnerId: " + geschaeftspartnerId, e);
        }

        String[] splitParts = GpidRangeGenerator.counterAndRangeSplitted(parsedGeschaeftspartnerId);

        CreateSubjectAreaUnitAnfrageDTO createSubjectAreaUnitAnfrageDTO = new CreateSubjectAreaUnitAnfrageDTO();

        createSubjectAreaUnitAnfrageDTO.setObjaddress(fileProperties.getObjaddress());
        createSubjectAreaUnitAnfrageDTO.setBasenr(fileProperties.getBasenr() + splitParts[0]);
        createSubjectAreaUnitAnfrageDTO.setShortterm(splitParts[1]);

        return createSubjectAreaUnitAnfrageDTO;
    }

}




