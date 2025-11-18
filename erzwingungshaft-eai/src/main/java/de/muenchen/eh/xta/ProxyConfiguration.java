package de.muenchen.eh.xta;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfiguration {

    private Protocol http;
    private Protocol https;

    @Setter
    @Getter
    public static class Protocol {
        private String host;
        private int port;
    }

    @PostConstruct
    public void init() {
        if (http != null) {
            System.setProperty("http.proxyHost", http.getHost());
            System.setProperty("http.proxyPort", String.valueOf(http.getPort()));
        }

        if (https != null) {
            System.setProperty("https.proxyHost", https.getHost());
            System.setProperty("https.proxyPort", String.valueOf(https.getPort()));
        }
    }

}
