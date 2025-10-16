package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import de.muenchen.eh.log.db.entity.ClaimEfile;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class FileDTOBuilder {

    private final ClaimProcessingContentWrapper contentWrapper;

    private final CreateFileDTO createFileDTO = new CreateFileDTO();

    public static FileDTOBuilder create(ClaimProcessingContentWrapper contentWrapper) {
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
