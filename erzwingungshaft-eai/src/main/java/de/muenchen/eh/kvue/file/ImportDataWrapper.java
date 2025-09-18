package de.muenchen.eh.kvue.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImportDataWrapper {

    private String claimRawData;
    private ClaimImportData claimImportData;

}
