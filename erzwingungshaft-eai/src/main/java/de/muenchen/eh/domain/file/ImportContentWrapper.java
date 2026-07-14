package de.muenchen.eh.domain.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImportContentWrapper {

    private String claimRawData;
    private ImportClaimIdentifierData importClaimIdentifierData;

}

