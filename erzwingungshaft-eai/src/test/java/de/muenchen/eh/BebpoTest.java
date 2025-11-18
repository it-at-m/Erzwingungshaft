package de.muenchen.eh;

import de.muenchen.eh.kvue.claim.ClaimRouteBuilder;
import de.muenchen.eh.kvue.file.FileImportRouteBuilder;
import de.muenchen.eh.log.db.repository.ClaimContentRepository;
import de.muenchen.eh.log.db.repository.ClaimDataRepository;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.log.db.repository.ClaimEfileRepository;
import de.muenchen.eh.log.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.log.db.repository.ClaimImportRepository;
import de.muenchen.eh.log.db.repository.ClaimLogRepository;
import de.muenchen.eh.log.db.repository.ClaimRepository;
import de.muenchen.eh.log.db.repository.ClaimXmlRepository;
import de.muenchen.eh.xta.XtaContext;
import de.muenchen.eh.xta.XtaRouteBuilder;
import genv3.de.xoev.transport.xta.x211.LookupServiceRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.ExcludeRoutes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@CamelSpringBootTest
//@SpringBootTest(classes = {Application.class, XtaContext.class}, properties = {"camel.springboot.java-routes-include-pattern=**/XtaRouteBuilder,**/BaseRouteBuilder" })
@SpringBootTest(classes = {Application.class, XtaContext.class})
//@TestPropertySource(locations = { "classpath:application-test.yml" }, properties = "debug=false")
@ExcludeRoutes({FileImportRouteBuilder.class, ClaimRouteBuilder.class})
@ActiveProfiles(profiles = {TestConstants.SPRING_TEST_PROFILE})
@TestPropertySource(properties = "spring.flyway.enabled=false")
@TestPropertySource(properties =  "spring.sql.init.mode=never")
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})
public class BebpoTest {

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

    @Autowired
    private CamelContext camelContext;

    @Test
    public void bebpoTest() {

        given(entityManagerFactory.createEntityManager())
                .willReturn(mock(EntityManager.class));


       LookupServiceRequest.LookupServiceRequestList list = new LookupServiceRequest.LookupServiceRequestList();
       list.setLookupService(null);


       genv3.de.xoev.transport.xta.x211.LookupServiceRequest lookupServiceRequest = new genv3.de.xoev.transport.xta.x211.LookupServiceRequest();
       lookupServiceRequest.getLookupServiceRequestList().add(list);

        Exchange request = ExchangeBuilder.anExchange(camelContext).withBody(lookupServiceRequest).build();


        Exchange response = managementPort.send(request);
        assertNull(response.getAllProperties().get(Exchange.EXCEPTION_CAUGHT));

    }

}
