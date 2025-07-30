package de.muenchen.eh;

import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@CamelSpringBootTest
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class ProcessKVUEDataTest extends TestSupport {

    @EndpointInject("mock:xjustizXml")
    private MockEndpoint xjustizXml;

    @EndpointInject("mock:error")
    private MockEndpoint failures;

    @Test
    void test_readDataAndCreateXustizXml() throws Exception {

        xjustizXml.expectedMessageCount(3);
        xjustizXml.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        failures.expectedMessageCount(2);
        failures.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 lastXJustizMessage = parseXML(xjustizXml.getExchanges().getLast().getMessage().getBody(String.class));

        var betroffener = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getFirst().getBeteiligter().getAuswahlBeteiligter().getNatuerlichePerson();

        assertEquals("TESTILIMON", betroffener.getVollerName().getNachname());
        assertEquals("CXXXXX", betroffener.getVollerName().getVorname());

        var exception = (Exception) failures.getReceivedExchanges().getLast().getAllProperties().get(Exchange.EXCEPTION_CAUGHT);
        assertEquals("The mandatory field defined at the position 31 is empty for the line: 1", exception.getMessage());

    }

}
