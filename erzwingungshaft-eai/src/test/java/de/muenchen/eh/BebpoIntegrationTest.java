package de.muenchen.eh;

import de.muenchen.eh.claim.ClaimRouteBuilder;
import de.muenchen.eh.file.FileImportRouteBuilder;
import de.muenchen.eh.db.repository.ClaimContentRepository;
import de.muenchen.eh.db.repository.ClaimDataRepository;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.db.repository.ClaimImportRepository;
import de.muenchen.eh.db.repository.ClaimLogRepository;
import de.muenchen.eh.db.repository.ClaimRepository;
import de.muenchen.eh.db.repository.ClaimXmlRepository;
import de.muenchen.eh.claim.xta.transport.ByteArrayDataSource;
import de.muenchen.eh.claim.xta.transport.StringDataSource;
import de.muenchen.eh.claim.xta.transport.properties.XtaClientConfiguration;
import de.muenchen.eh.claim.xta.XtaRouteBuilder;
import genv3.de.xoev.transport.xta.x211.ContentType;
import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import genv3.eu.osci.ws.x2008.x05.transport.X509TokenContainerType;
import genv3.eu.osci.ws.x2014.x10.transport.DeliveryAttributesType;
import genv3.eu.osci.ws.x2014.x10.transport.DestinationsType;
import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import genv3.eu.osci.ws.x2014.x10.transport.MsgIdentificationType;
import genv3.eu.osci.ws.x2014.x10.transport.OriginatorsType;
import genv3.eu.osci.ws.x2014.x10.transport.PartyIdentifierType;
import genv3.eu.osci.ws.x2014.x10.transport.PartyType;
import genv3.eu.osci.ws.x2014.x10.transport.QualifierType;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.xml.soap.SOAPException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.ExcludeRoutes;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@CamelSpringBootTest
@SpringBootTest(classes = {Application.class, XtaTestContext.class})
@ExcludeRoutes({FileImportRouteBuilder.class, ClaimRouteBuilder.class})
@ActiveProfiles(profiles = {TestConstants.SPRING_TEST_PROFILE})
@TestPropertySource(properties = "spring.flyway.enabled=false")
@TestPropertySource(properties = "spring.sql.init.mode=never")
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})
@Slf4j
@Disabled("The tests are less junit tests and more bebpo integration tests to try something out.")
public class BebpoIntegrationTest {

    @MockitoBean
    private ClaimContentRepository claimContentRepository;
    @MockitoBean
    private ClaimDataRepository claimDataRepository;
    @MockitoBean
    private ClaimDocumentRepository claimDocumentRepository;
    @MockitoBean
    private ClaimEfileRepository claimEfileRepository;
    @MockitoBean
    private ClaimImportLogRepository claimImportLogRepository;
    @MockitoBean
    private ClaimImportRepository claimImportRepository;
    @MockitoBean
    private ClaimLogRepository claimLogRepository;
    @MockitoBean
    private ClaimRepository claimRepository;
    @MockitoBean
    private ClaimXmlRepository claimXmlRepository;
    @MockitoBean(name = "entityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    @Produce(XtaRouteBuilder.BEPBO_MANAGEMENT_PORT)
    private ProducerTemplate managementPort;
    @Produce(XtaRouteBuilder.BEPBO_SEND_PORT)
    private ProducerTemplate sendPort;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private XtaClientConfiguration xtaClientConfig;

    @Test
    public void test_bebpo_checkAccountActive() {

        given(entityManagerFactory.createEntityManager())
                .willReturn(mock(EntityManager.class));

        Exchange request = ExchangeBuilder.anExchange(camelContext)
                .withBody(Collections.emptyList())
                .withHeader(CxfConstants.OPERATION_NAME, "checkAccountActive")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .withHeader("SOAPAction","http://www.xta.de/XTA/CheckAccountActive")
                .build();

        Exchange response = managementPort.send(request);
        assertNull(response.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

    }

    @Test
    public void test_bebpo_createMessageId() {

        given(entityManagerFactory.createEntityManager())
                .willReturn(mock(EntityManager.class));

        Exchange request = ExchangeBuilder.anExchange(camelContext)
                .withBody(Collections.emptyList())
                .withHeader(CxfConstants.OPERATION_NAME, "createMessageId")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange response = managementPort.send(request);
        assertNull(response.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

        AttributedURIType attributedURIType = response.getIn().getBody(AttributedURIType.class);
        assertNotNull(attributedURIType.getValue());
        assertTrue(attributedURIType.getValue().startsWith("urn:de:xta:messageid:governikusmultimessenger"));

    }

    @Test
    public void test_bebpo_sendMessage() throws SOAPException, IOException {

        // Create message id
        given(entityManagerFactory.createEntityManager())
                .willReturn(mock(EntityManager.class));

        Exchange requestMessageId = ExchangeBuilder.anExchange(camelContext)
                .withBody(Collections.emptyList())
                .withHeader(CxfConstants.OPERATION_NAME, "createMessageId")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange responseMessageId = managementPort.send(requestMessageId);
        assertNull(responseMessageId.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

        AttributedURIType attributedURIType = responseMessageId.getIn().getBody(AttributedURIType.class);

        log.info("MessageId : "  + attributedURIType.getValue());

        // Send message
        GenericContentContainer genericContentContainer = new GenericContentContainer();
        GenericContentContainer.ContentContainer contentContainer = new GenericContentContainer.ContentContainer();
        ContentType textType = new ContentType();
        textType.setContentType("text/plain");
        textType.setEncoding("UTF-8");
        textType.setContentDescription("Test Description");
        DataSource textMessage = new StringDataSource(Base64.getEncoder().encodeToString("Test-ehaft-message".getBytes()), "text/plain", "message");
        DataHandler textDataHandler = new DataHandler(textMessage);
        textType.setValue(textDataHandler);
        contentContainer.setMessage(textType);

        ContentType justizMessageType = new ContentType();
        justizMessageType.setContentType("application/xml");
        justizMessageType.setEncoding("UTF-8");
        justizMessageType.setFilename("xjustiz.xml");
        justizMessageType.setContentDescription("Test Description");
        DataSource justizMessage = new StringDataSource(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of("src/test/resources/xjustiz.xml"))), "application/xml", "xjustiz.xml");
        DataHandler justizDataHandler = new DataHandler(justizMessage);
        justizMessageType.setValue(justizDataHandler);
        contentContainer.getAttachment().add(justizMessageType);

        ContentType antragMessageType = new ContentType();
        antragMessageType.setContentType("application/pdf");
        antragMessageType.setFilename("1000013749_5793303492524_20240807_EH.pdf");
        antragMessageType.setContentDescription("EH-Antrag");
        DataSource antragMessage = new ByteArrayDataSource(Files.readAllBytes(Path.of("testdata/in/pdf/1000013749_5793303492524_20240807_EH.pdf")), "application/xml", "1000013749_5793303492524_20240807_EH.pdf");
        DataHandler antragDataHandler = new DataHandler(antragMessage);
        antragMessageType.setValue(antragDataHandler);
        contentContainer.getAttachment().add(antragMessageType);

        ContentType bescheidMessageType = new ContentType();
        bescheidMessageType.setContentType("application/pdf");
        bescheidMessageType.setFilename("1000013749_5793303492524_20240807_URB.pdf");
        bescheidMessageType.setContentDescription("EH-Bescheid");
        DataSource bescheidMessage = new ByteArrayDataSource(Files.readAllBytes(Path.of("testdata/in/pdf/1000013749_5793303492524_20240807_URB.pdf")), "application/xml", "1000013749_5793303492524_20240807_URB.pdf");
        DataHandler bescheidDataHandler = new DataHandler(bescheidMessage);
        bescheidMessageType.setValue(bescheidDataHandler);
        contentContainer.getAttachment().add(bescheidMessageType);

        genericContentContainer.setContentContainer(contentContainer);

        final MessageMetaData messageMetaData = new MessageMetaData();

        final MsgIdentificationType msgIdentificationType = new MsgIdentificationType();
        msgIdentificationType.setMessageID(attributedURIType);
        messageMetaData.setMsgIdentification(msgIdentificationType);

        DeliveryAttributesType deliveryAttributesType = new DeliveryAttributesType();
        deliveryAttributesType.setOrigin(DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        messageMetaData.setDeliveryAttributes(deliveryAttributesType);

        OriginatorsType originatorsType = new OriginatorsType();
        PartyType originator = new PartyType();
        PartyIdentifierType authorIdentifierType = new PartyIdentifierType();
        authorIdentifierType.setName("KVUE EHaft");
        authorIdentifierType.setValue(xtaClientConfig.getPartyIdentifier().getOriginator());
        authorIdentifierType.setType("XTA_ID");
        originator.setIdentifier(authorIdentifierType);
        originatorsType.setAuthor(originator);
        messageMetaData.setOriginators(originatorsType);

        DestinationsType destinations = new DestinationsType();
        PartyType reader = new PartyType();
        PartyIdentifierType readerIdentifierType = new PartyIdentifierType();
        readerIdentifierType.setValue(xtaClientConfig.getPartyIdentifier().getDestination());
        reader.setIdentifier(readerIdentifierType);
        destinations.setReader(reader);
        messageMetaData.setDestinations(destinations);

        final QualifierType qualifierType = new QualifierType();
        qualifierType.setSubject("ehaft-test-attachment");
        qualifierType.setService("");

        QualifierType.MessageType qualifierMessageType = new QualifierType.MessageType();
        qualifierMessageType.setListURI("urn:de:bos_bremen:gov:gateway:messageTypes");
        qualifierMessageType.setCode("XTA");
        qualifierMessageType.setListVersionID("1.0");
        qualifierMessageType.setPayloadSchema("http://xoev.de/transport/xta/211");
        qualifierType.setMessageType(qualifierMessageType);
        messageMetaData.setQualifier(qualifierType);

        Exchange requestSend = ExchangeBuilder.anExchange(camelContext)
                .withBody(List.of(genericContentContainer, messageMetaData, new X509TokenContainerType()))
                .withHeader("MessageID", attributedURIType.getValue())
                .build();

        Exchange responseSend = sendPort.send(requestSend);
        assertNull(responseSend.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

        // Get transport report
        Exchange requestTransportReport = ExchangeBuilder.anExchange(camelContext)
                .withBody(List.of(attributedURIType, originator))
                .withHeader(CxfConstants.OPERATION_NAME, "getTransportReport")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange responseTransportReport = managementPort.send(requestTransportReport);
        assertNull(responseTransportReport.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

    }

    @Test
    public void test_bebpo_receiveReport() {

        given(entityManagerFactory.createEntityManager())
                .willReturn(mock(EntityManager.class));

        AttributedURIType attributedURIType = new AttributedURIType();
        attributedURIType.setValue("changeme");

        PartyType originator = new PartyType();
        PartyIdentifierType authorIdentifierType = new PartyIdentifierType();
        authorIdentifierType.setName("KVUE EHaft");
        authorIdentifierType.setValue(xtaClientConfig.getPartyIdentifier().getOriginator());
        authorIdentifierType.setType("XTA_ID");
        originator.setIdentifier(authorIdentifierType);

        Exchange requestTransportReport = ExchangeBuilder.anExchange(camelContext)
                .withBody(List.of(attributedURIType, originator))
                .withHeader(CxfConstants.OPERATION_NAME, "getTransportReport")
                .withHeader(CxfConstants.OPERATION_NAMESPACE, "http://xoev.de/transport/xta/211")
                .build();

        Exchange responseTransportReport = managementPort.send(requestTransportReport);
        assertNull(responseTransportReport.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

    }

}
