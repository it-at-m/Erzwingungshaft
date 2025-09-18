package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.entity.ImportEntity;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClaimDataWrapper {

    private ImportEntity importEntity;
    private ClaimData ehClaimData;
    private ContentContainer contentContainer;
    private String xjustizXml;

}
