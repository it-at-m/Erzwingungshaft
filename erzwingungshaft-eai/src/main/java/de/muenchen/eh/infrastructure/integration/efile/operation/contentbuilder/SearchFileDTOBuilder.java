package de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder;

import de.muenchen.eakte.api.rest.model.SearchFileRequestDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchFileDTOBuilder {

    private final String cooApentryAddress;

    public static SearchFileDTOBuilder create(String cooApentryAddress) {
        return new SearchFileDTOBuilder(cooApentryAddress);
    }

    public SearchFileRequestDTO build() {
        return createSearchFileRequestDTO();
    }

    private SearchFileRequestDTO createSearchFileRequestDTO() {

        SearchFileRequestDTO searchFileRequestDTO = new SearchFileRequestDTO();
        searchFileRequestDTO.setApentry(cooApentryAddress);
        return searchFileRequestDTO;
    }

}
