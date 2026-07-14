package de.muenchen.eh.infrastructure.integration.efile.properties;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "efile.fine")
public class FineProperties {

    private String shortname;
    private String accdef;
    private String doctemplate;
    private String subfiletype;
    private String incattachments;
    private String outgoing;
    private String ehVorgangDefinition;
    private Map<String, String> subjectDataValues;

}



