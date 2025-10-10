package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.entity.Claim;
import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ClaimProcessingContentWrapper {

    private ClaimImport claimImport;
    private Claim claim;
    private ImportClaimData ehImportClaimData;
    private ContentContainer contentContainer;
    private String xjustizXml;
    private Map<String, Object> efile = new HashMap<>();

}
