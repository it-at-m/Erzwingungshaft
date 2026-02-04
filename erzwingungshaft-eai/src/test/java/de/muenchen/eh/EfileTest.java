package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
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
class EfileTest extends TestContainerConfiguration {

    @Produce(value = EfileRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    @Autowired
    private OperationIdFactory operationIdFactory;

    @Autowired
    private CamelContext camelContext;

    @Test
    void test_readCollections() {

        Exchange exchange = new DefaultExchange(camelContext);
        Exchange readApentryRequest = operationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);

        Exchange eakteResponse = eakteConnector.send(readApentryRequest);
        assertNull(eakteResponse.getException());

        ReadApentryAntwortDTO readApentryAntwortDTO = eakteResponse.getIn().getBody(ReadApentryAntwortDTO.class);

        assertEquals(5, readApentryAntwortDTO.getGiobjecttype().size());
    }

}
