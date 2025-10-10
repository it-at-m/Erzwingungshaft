package de.muenchen.eh.kvue.claim.efile.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "efile.shortname")
public class ShortnameProperties {

    private String fine;
    private String outgoing;

}
