package de.muenchen.eh.domain.identifier;

import de.muenchen.eh.DataWrapper;
import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class IdentifierContentWrapper implements DataWrapper {

    private EfileIdentifier efileIdentifier;
    private PscdDataExport pscdDataExport;
    private Map<String, Object> efile = new HashMap<>();

}
