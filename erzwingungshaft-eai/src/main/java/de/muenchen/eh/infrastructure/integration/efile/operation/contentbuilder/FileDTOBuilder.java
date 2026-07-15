package de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.integration.efile.properties.FileProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDTOBuilder {

    private final FileProperties fileProperties;
    private final ClaimContentWrapper contentWrapper;

    private final CreateFileDTO createFileDTO = new CreateFileDTO();

    public static FileDTOBuilder create(FileProperties fileProperties, ClaimContentWrapper contentWrapper) {
        return new FileDTOBuilder(fileProperties, contentWrapper);
    }

    public CreateFileDTO build() {
        return createCaseFileDTO();
    }

    private CreateFileDTO createCaseFileDTO() {

        Optional<ClaimEfile> collection = Optional.ofNullable(contentWrapper.getClaimEfile());
        collection.ifPresent(coll -> {
            createFileDTO.setShortname(contentWrapper.getClaimImport().getGeschaeftspartnerId());
            createFileDTO.setFilesubj(contentWrapper.getEhImportClaimData().getZentralaktkennung());
            createFileDTO.setApentry(coll.getCollection());
            createFileDTO.setDefinition(fileProperties.getKmAkteDefinition());

        });

        return createFileDTO;
    }

}
