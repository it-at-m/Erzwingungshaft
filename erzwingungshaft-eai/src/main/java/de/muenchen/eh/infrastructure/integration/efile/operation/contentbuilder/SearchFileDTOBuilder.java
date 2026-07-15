package de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.SearchFileRequestDTO;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchFileDTOBuilder {

    private final ClaimContentWrapper contentWrapper;

    public static SearchFileDTOBuilder create(ClaimContentWrapper contentWrapper) {
        return new SearchFileDTOBuilder(contentWrapper);
    }

    public SearchFileRequestDTO build() {
        return createSearchFileRequestDTO();
    }

    private SearchFileRequestDTO createSearchFileRequestDTO() {

        SearchFileRequestDTO searchFileRequestDTO = new SearchFileRequestDTO();

        searchFileRequestDTO.setApentry(contentWrapper.getClaimEfile().getCollection());

        return searchFileRequestDTO;
    }

}
