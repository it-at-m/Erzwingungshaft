package de.muenchen.erzwingungshaft.xta.config;

import genv3.de.xoev.transport.xta.x211.XTAService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XtaServiceConfig {

    @Bean
    public XTAService xtaService(/* ggf. Abhängigkeiten hier injizieren */) {
        return new XTAService();
    }
}
