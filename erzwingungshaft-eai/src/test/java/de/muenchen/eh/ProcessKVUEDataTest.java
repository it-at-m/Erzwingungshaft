package de.muenchen.eh;

import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import org.apache.camel.EndpointInject;
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
    private MockEndpoint error;

    @Test
    void test_readDataAndCreateXustizXml() throws Exception {

        xjustizXml.expectedMessageCount(11);
        xjustizXml.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        error.expectedMessageCount(1);
        error.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 lastXJustizMessage = parseXML(xjustizXml.getExchanges().getLast().getMessage().getBody(String.class));

        var betroffener = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getFirst().getBeteiligter().getAuswahlBeteiligter().getNatuerlichePerson();

        assertEquals("Schxxx", betroffener.getVollerName().getNachname());
        assertEquals("Koxxxxx", betroffener.getVollerName().getVorname());

    }

}
