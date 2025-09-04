package de.muenchen.erzwingungshaft;

import de.muenchen.erzwingungshaft.xta.config.XtaClientConfig;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(XtaClientConfig.class)
@SuppressWarnings("PMD.UseUtilityClass")
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
