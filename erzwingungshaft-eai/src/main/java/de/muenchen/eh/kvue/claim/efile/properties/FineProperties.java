package de.muenchen.eh.kvue.claim.efile.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "efile.fine")
public class FineProperties {

    private String shortname;
    private String filesubj;
    private String accdef;
    private String doctemplate;
    private String subfiletype;
    private String incattachments;
    private String outgoing;
    private Map<String, String> subjectDataValues;

}
