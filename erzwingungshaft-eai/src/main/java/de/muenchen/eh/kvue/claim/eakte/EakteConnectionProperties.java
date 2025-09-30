package de.muenchen.eh.kvue.claim.eakte;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "eakte.connection")
public class EakteConnectionProperties {

    private String host;
    private int port;
    private String scheme;
    private String contextPath;
    private String username;
    private String password;

}
