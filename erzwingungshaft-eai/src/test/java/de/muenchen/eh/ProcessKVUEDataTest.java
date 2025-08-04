package de.muenchen.eh;

import de.muenchen.eh.common.XmlUnmarshaller;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.EntryEntity;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.LogRepository;
import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@CamelSpringBootTest
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class ProcessKVUEDataTest {

    @EndpointInject("mock:xjustizXml")
    private MockEndpoint xjustizXml;

    @EndpointInject("mock:error")
    private MockEndpoint failures;

    @Autowired
    private LogRepository logRepository;

    @Test
    void test_readDataAndCreateXustizXml() throws Exception {

        xjustizXml.expectedMessageCount(3);
        xjustizXml.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        failures.expectedMessageCount(2);
        failures.assertIsSatisfied(TimeUnit.SECONDS.toMillis(1));

        NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 lastXJustizMessage = XmlUnmarshaller.unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(xjustizXml.getExchanges().getLast().getMessage().getBody(String.class));

        var betroffener = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getFirst().getBeteiligter().getAuswahlBeteiligter().getNatuerlichePerson();

        assertEquals("TESTILIMON", betroffener.getVollerName().getNachname());
        assertEquals("CXXXXX", betroffener.getVollerName().getVorname());

        var beteiligung = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getLast();
        assertEquals("046", beteiligung.getRolles().getFirst().getRollenbezeichnung().getCode());

        assertEquals("Stadt München",lastXJustizMessage.getNachrichtenkopf().getAbsender().getInformationen().getAuswahlKommunikationspartner().getSonstige());

        var exchange = failures.getReceivedExchanges().getLast();
        var exception = (Exception) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT);
        assertEquals("The mandatory field defined at the position 31 is empty for the line: 1", exception.getMessage());

        var entry = exchange.getIn().getHeader(Constants.ENTRY_ENTITY, EntryEntity.class);
        var logs = logRepository.findByEntryIdAndMessageTyp(entry.getId(), MessageType.ERROR);
        assertEquals("The mandatory field defined at the position 31 (de.muenchen.eh.kvue.EhCase.ehtatstdb) is empty for the line: 1", logs.getLast().getMessage());

    }

}
