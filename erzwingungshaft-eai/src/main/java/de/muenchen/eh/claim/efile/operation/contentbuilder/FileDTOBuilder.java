package de.muenchen.eh.claim.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.ImportClaimData;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.entity.ClaimImport;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDTOBuilder {

    private final ClaimContentWrapper contentWrapper;

    private final CreateFileDTO createFileDTO = new CreateFileDTO();

    public static FileDTOBuilder create(ClaimContentWrapper contentWrapper) {
        return new FileDTOBuilder(contentWrapper);
    }

    public CreateFileDTO build() {
        return createCaseFileDTO();
    }

    private CreateFileDTO createCaseFileDTO() {

        Optional<ClaimEfile> collection = Optional.ofNullable(contentWrapper.getClaimEfile());
        collection.ifPresent(coll -> {
            createFileDTO.setApentry(coll.getCollection());
        });

        Optional<ClaimImport> claimImport = Optional.ofNullable(contentWrapper.getClaimImport());
        claimImport.ifPresent(ci -> {
            createFileDTO.setShortname(ci.getGeschaeftspartnerId());
        });

        Optional<ImportClaimData> claimImportData = Optional.ofNullable(contentWrapper.getEhImportClaimData());
        claimImportData.ifPresent(icd -> {
            createFileDTO.setFilesubj(icd.getZentralaktkennung());
        });

        return createFileDTO;
    }

}
