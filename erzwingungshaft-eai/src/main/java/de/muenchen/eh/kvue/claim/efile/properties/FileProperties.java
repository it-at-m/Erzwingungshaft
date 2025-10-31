package de.muenchen.eh.kvue.claim.efile.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "efile.case-file")
public class FileProperties {

    private String xanwendung;
    private String aktenplanEintrag;
    private String userlogin;
    private String joboe;
    private String jobposition;
    private String objaddress;
    private Map<String, String> subjectDataValues;

}
