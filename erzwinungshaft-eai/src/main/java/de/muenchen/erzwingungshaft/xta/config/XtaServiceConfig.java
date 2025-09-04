package de.muenchen.erzwingungshaft.xta.config;

import de.muenchen.erzwingungshaft.xta.core.XtaClientService;
import de.muenchen.erzwingungshaft.xta.core.XtaServicePorts;
import de.muenchen.erzwingungshaft.xta.core.XtaServicePortsFactory;
import de.muenchen.erzwingungshaft.xta.exception.XtaClientInitializationException;
import genv3.de.xoev.transport.xta.x211.XTAService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XtaServiceConfig {

    private final XtaServicePortsFactory xtaServicePortsFactory;

    @Bean
    public XTAService xtaService() {
        return new XTAService();
    }

    @Bean
    public XtaServicePorts xtaServicePorts() throws XtaClientInitializationException {
        return xtaServicePortsFactory.create();
    }
}
