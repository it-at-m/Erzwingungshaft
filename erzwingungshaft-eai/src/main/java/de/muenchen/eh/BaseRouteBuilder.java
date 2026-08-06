package de.muenchen.eh;

import de.muenchen.eh.infrastructure.log.Constants;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseRouteBuilder extends RouteBuilder {

    protected static final String GLOBAL_EXCEPTION_HANDLER = "direct:globalExceptionHandler";

    @Value("${redelivery.max:3}")
    protected Integer maxRedeliveries;

    @Value("${redelivery.delay:5000}")
    protected Integer redeliveriesDelay;

    @Value("${xjustiz.interface.file.line-break}")
    protected String lineBreak;

    @Override
    public void configure() {

        // spotless:off
        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .choice()
                    .when(exchangeProperty(Constants.CLAIM).isNotNull())
                    .bean("logServiceClaim", "logIllegalArgumentException")
                    .otherwise()
                    .log(LoggingLevel.ERROR, "${exception.stacktrace}")
                .end()
                .process(new StopExchange());

        onException(Exception.class)
                .handled(true)
                .to(GLOBAL_EXCEPTION_HANDLER);

        onException(ConnectException.class, SocketTimeoutException.class)
                .maximumRedeliveries(maxRedeliveries)
                .redeliveryDelay(redeliveriesDelay)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .handled(true)
                .to(GLOBAL_EXCEPTION_HANDLER);

        // spotless:on

    }

}
