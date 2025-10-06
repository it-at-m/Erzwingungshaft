package de.muenchen.eh.kvue.claim.eakte.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "eakte.einzelakten")
public class AktenplanEinzelaktenProperties {

    private String xanwendung;
    private String aktenplanEintrag;
    private String userlogin;
    private String joboe;
    private String jobposition;
    private String objaddress;

}
