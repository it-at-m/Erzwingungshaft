package de.muenchen.eh.claim.efile.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "efile.connection")
public class ConnectionProperties {

    private String host;
    private int port;
    private String scheme;
    private String contextPath;
    private String username;
    private String password;
    private String eakteApiVersion;

}
