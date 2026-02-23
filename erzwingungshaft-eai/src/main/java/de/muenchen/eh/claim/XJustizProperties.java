package de.muenchen.eh.claim;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "xjustiz")
public class XJustizProperties {

    private String version;
    private Xsd xsd;
    private XJustiz0500Straf xjustiz0500straf;

    @Data
    public static class Xsd {
        private String path;
        private String name;
    }

    @Data
    public static class XJustiz0500Straf {
        private Xsd xsd;
    }

}
