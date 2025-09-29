package de.muenchen.eh;

import de.muenchen.eh.eakte.*;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {Application.class },properties = {"camel.main.java-routes-include-pattern=**/EakteRouteBuilder" })
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
public class EakteTest {

    @Produce(value= EakteRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    @Autowired
    private EakteConnectionProperties connectionProperties;

    @Autowired
    private EakteOperationIdFactory eakteOperationIdFactory;

    @Autowired
    private CamelContext camelContext;

    @EndpointInject("mock:error")
    private MockEndpoint failures;

    @Test
    void test_readApentryEakte() throws InterruptedException {

        failures.expectedMessageCount(0);

        Exchange readApentryRequest = eakteOperationIdFactory.createExchange(OperationId.READ_APENTRY);
        Exchange eakteResponse = eakteConnector.send(readApentryRequest);
        assertNull(eakteResponse.getException());

        failures.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        assertTrue(true);
    }


}
