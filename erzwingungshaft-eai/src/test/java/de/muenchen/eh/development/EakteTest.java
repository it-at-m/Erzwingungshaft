package de.muenchen.eh.development;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.eh.Application;
import de.muenchen.eh.TestConstants;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.db.entity.Claim;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { Application.class }, properties = { "camel.main.java-routes-include-pattern=**/EfileRouteBuilder" })
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
@Disabled("Only works with running stack/docker-compose.yml")
class EakteTest {

    @Produce(value = EfileRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    @Autowired
    private OperationIdFactory operationIdFactory;

    @Autowired
    private CamelContext camelContext;

    @EndpointInject("mock:error")
    private MockEndpoint failures;

    @Test
    void test_readApentryEakte() throws InterruptedException {

        failures.expectedMessageCount(0);

        // Must be set manually in this test case.
        Claim testClaim = new Claim();
        testClaim.setId(1);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(testClaim);

        Exchange readApentryRequest = operationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);

        /*
         * Mock eakte request ....
         */

        Exchange eakteResponse = eakteConnector.send(readApentryRequest);
        assertNull(eakteResponse.getException());

        failures.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        assertTrue(true);
    }

}
