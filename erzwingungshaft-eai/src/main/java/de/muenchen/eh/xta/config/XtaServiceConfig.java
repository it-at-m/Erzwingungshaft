package de.muenchen.eh.xta.config;

import de.muenchen.eh.xta.core.XtaServicePorts;
import de.muenchen.eh.xta.core.XtaServicePortsFactory;
import de.muenchen.eh.xta.exception.XtaClientInitializationException;
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
