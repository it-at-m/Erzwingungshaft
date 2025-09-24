package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClaimProcessingContentWrapper {

    private ClaimImport claimImport;
    private ImportClaimData ehImportClaimData;
    private ContentContainer contentContainer;
    private String xjustizXml;

}
