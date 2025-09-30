package de.muenchen.eh;

import de.muenchen.eh.kvue.claim.eakte.EakteOperationIdFactory;
import de.muenchen.eh.kvue.claim.eakte.EakteRouteBuilder;
import de.muenchen.eh.kvue.claim.eakte.OperationId;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.Claim;
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
    private EakteOperationIdFactory eakteOperationIdFactory;

    @EndpointInject("mock:error")
    private MockEndpoint failures;

    @Test
    void test_readApentryEakte() throws InterruptedException {

        failures.expectedMessageCount(0);

        // Must be set manually in this test case.
        Claim testClaim = new Claim();
        testClaim.setId(1);

        Exchange readApentryRequest = eakteOperationIdFactory.createExchange(OperationId.READ_APENTRY, testClaim);


        /*
            Mock eakte request ....
         */


        Exchange eakteResponse = eakteConnector.send(readApentryRequest);
        assertNull(eakteResponse.getException());

        failures.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        assertTrue(true);
    }


}
