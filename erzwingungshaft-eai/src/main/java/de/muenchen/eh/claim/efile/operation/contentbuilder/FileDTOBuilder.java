package de.muenchen.eh.claim.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.db.entity.ClaimEfile;
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
            createFileDTO.setShortname(contentWrapper.getClaimImport().getGeschaeftspartnerId());
            createFileDTO.setApentry(coll.getCollection());
        });

        return createFileDTO;
    }

}
