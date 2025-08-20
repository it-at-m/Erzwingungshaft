package de.muenchen.erzwingungshaft.xta.core;

import de.muenchen.erzwingungshaft.xta.config.XtaClientConfig;
import de.muenchen.erzwingungshaft.xta.exception.XtaClientInitializationException;
import genv3.de.xoev.transport.xta.x211.XTAService;
import jakarta.xml.ws.BindingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.AbstractLoggingInterceptor;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class XtaServicePortsFactory {

    private static final String SCHEMA_VALIDATION_ENABLED_KEY = Message.SCHEMA_VALIDATION_ENABLED;

    private final XTAService xtaService;
    private final XtaClientConfig config;
    private final TlsClientParametersFactory tlsClientParametersFactory;

    public XtaServicePorts create() throws XtaClientInitializationException {
        return new XtaServicePorts(
                configureXtaServicePort(config.getManagementPortUri(), xtaService.getManagementPort()),
                configureXtaServicePort(config.getMsgBoxportUri(), xtaService.getMsgBoxPort()),
                configureXtaServicePort(config.getSendPortUri(), xtaService.getSendXtaPort()));
    }

    private <T> T configureXtaServicePort(final String endpointUrl, final T port) throws XtaClientInitializationException {
        var bindingProvider = (BindingProvider) port;
        configureRequestContext(endpointUrl, bindingProvider.getRequestContext());
        configureClient(ClientProxy.getClient(bindingProvider));
        return port;
    }

    private void configureRequestContext(final String endpointUrl, Map<String, Object> requestContext) {
        requestContext.putAll(Map.of(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl,
                SCHEMA_VALIDATION_ENABLED_KEY, config.isSchemaValidation()
        ));
    }

    private void configureClient(Client client) throws XtaClientInitializationException {
        if (config.isLogSoapRequests()) {
            addInterceptorPair(client.getInInterceptors(), client.getInFaultInterceptors(), LoggingInInterceptor::new);
        }

        if (config.isLogSoapResponses()) {
            addInterceptorPair(client.getOutInterceptors(), client.getOutFaultInterceptors(), LoggingOutInterceptor::new);
        }

        configureHttpConduit((HTTPConduit) client.getConduit());
        log.debug("[configureClient] Initialized TransportSecurity + HTTP policy");
    }

    private <T extends AbstractLoggingInterceptor> void addInterceptorPair(List<Interceptor<? extends Message>> normal, List<Interceptor<? extends Message>> fault, Supplier<T> interceptorFactory) {
        normal.add(createConfiguredInterceptor(interceptorFactory.get()));
        fault.add(createConfiguredInterceptor(interceptorFactory.get()));
    }

    private <T extends AbstractLoggingInterceptor> T createConfiguredInterceptor(T interceptor) {
        interceptor.setPrettyLogging(true);
        interceptor.setLogBinary(true);
        interceptor.setLogMultipart(true);
        return interceptor;
    }

    private void configureHttpConduit(HTTPConduit conduit) throws XtaClientInitializationException {
        HTTPClientPolicy http = new HTTPClientPolicy();
        http.setConnectionTimeout(config.getConnectionTimeout());
        http.setReceiveTimeout(config.getReceiveTimeout());
        http.setConnectionRequestTimeout(config.getConnectionRequestTimeout());
        http.setAutoRedirect(true);
        http.setAllowChunking(true);

        conduit.setClient(http);
        conduit.setTlsClientParameters(tlsClientParametersFactory.create());
    }
}
