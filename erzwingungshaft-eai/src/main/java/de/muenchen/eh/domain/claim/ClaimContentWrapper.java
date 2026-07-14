package de.muenchen.eh.domain.claim;

import de.muenchen.eh.infrastructure.db.entity.Claim;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.db.entity.ClaimImport;
import de.muenchen.xjustiz.xjustiz0500straf.nachricht.straf.owi.verfahrensmitteilung.extern.an.justiz0500010.content.ContentContainer;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClaimContentWrapper {

    private ClaimImport claimImport;
    private Claim claim;
    private ImportClaimData ehImportClaimData;
    private ContentContainer contentContainer;
    private String xjustizXml;
    private ClaimEfile claimEfile;
    private Map<String, Object> efile = new HashMap<>();

}


