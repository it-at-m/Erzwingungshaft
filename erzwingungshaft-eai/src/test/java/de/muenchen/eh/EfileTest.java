package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.infrastructure.integration.efile.EfileRouteBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete.CompleteOperationIdFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@UseAdviceWith
@SpringBootTest(classes = { Application.class }, properties = { "camel.main.java-routes-include-pattern=**/EfileRouteBuilder" })
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class EfileTest extends TestContainerConfiguration {

    @Produce(value = EfileRouteBuilder.DMS_CONNECTION)
    private ProducerTemplate eakteConnector;

    @Autowired
    private CompleteOperationIdFactory completeOperationIdFactory;

    @Autowired
    private CamelContext camelContext;

    @Test
    void test_readCollections() throws Exception {

        AdviceWith.adviceWith(camelContext, "rest-openapi-eakte", a -> {
            a.weaveById("openapi-client").before()
                    .process(exchange -> exchange.getMessage().setHeader("TestCase", "EFileTest.test_readCollections"));
            ;
        });

        camelContext.start();

        Exchange exchange = new DefaultExchange(camelContext);
        Exchange readApentryRequest = completeOperationIdFactory.createExchange(OperationId.READ_COLLECTIONS, exchange);

        Exchange eakteResponse = eakteConnector.send(readApentryRequest);
        assertNull(eakteResponse.getException());

        ReadApentryAntwortDTO readApentryAntwortDTO = eakteResponse.getIn().getBody(ReadApentryAntwortDTO.class);

        assertEquals(5, readApentryAntwortDTO.getGiobjecttype().size());
    }

}
