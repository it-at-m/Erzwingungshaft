package de.muenchen.eh.eakte;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "eakte.object")
public class EakteObjectProperties {

    private String xanwendung;
    private String userlogin;
    private String joboe;
    private String jobposition;
    private String objaddress;

}
