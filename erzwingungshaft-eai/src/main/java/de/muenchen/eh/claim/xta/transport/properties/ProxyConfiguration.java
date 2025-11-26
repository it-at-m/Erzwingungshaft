package de.muenchen.eh.claim.xta.transport.properties;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        private List<String> nonProxyHosts;
    }

    @PostConstruct
    public void init() {
        if (http != null) {
            System.setProperty("http.proxyHost", http.getHost());
            System.setProperty("http.proxyPort", String.valueOf(http.getPort()));
            Optional.ofNullable(http.getNonProxyHosts()).ifPresent(hosts -> {
                String join = hosts.stream().collect(Collectors.joining("|"));
                System.setProperty("http.nonProxyHosts", join);
            });

        }

        if (https != null) {
            System.setProperty("https.proxyHost", https.getHost());
            System.setProperty("https.proxyPort", String.valueOf(https.getPort()));
            Optional.ofNullable(https.getNonProxyHosts()).ifPresent(hosts -> {
                String join = hosts.stream().collect(Collectors.joining("|"));
                System.setProperty("http.nonProxyHosts", join);
            });
        }
    }

}
