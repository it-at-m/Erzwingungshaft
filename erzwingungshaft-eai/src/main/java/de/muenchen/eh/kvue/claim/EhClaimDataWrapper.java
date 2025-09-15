package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.entity.ImportEntity;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EhClaimDataWrapper  {

    private ImportEntity importEntity;
    private EhClaimData ehClaimData;
    private ContentContainer contentContainer;
    private String xjustizXml;

}
