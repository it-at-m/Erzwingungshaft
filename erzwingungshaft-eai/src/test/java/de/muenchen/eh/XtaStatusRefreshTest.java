package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.eh.claim.ClaimRouteBuilder;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.xta.XtaRouteBuilder;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.entity.Xta;
import de.muenchen.eh.db.repository.ClaimImportRepository;
import de.muenchen.eh.db.repository.XtaRepository;
import de.muenchen.eh.file.FileImportRouteBuilder;
import de.xoev.transport.xta._211.MessageStatusType;
import de.xoev.transport.xta._211.TransportReport;
import java.math.BigInteger;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.ExcludeRoutes;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@UseAdviceWith
@SpringBootTest(classes = { Application.class, XtaTestContext.class })
@ExcludeRoutes({ FileImportRouteBuilder.class, ClaimRouteBuilder.class, EfileRouteBuilder.class })
@CamelSpringBootTest
@DirtiesContext
@EnableAutoConfiguration
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
public class XtaStatusRefreshTest extends TestContainerConfiguration {

    @Autowired
    protected ClaimImportRepository claimImportRepository;

    @Autowired
    protected XtaRepository xtaRepository;

    @Autowired
    protected CamelContext camelContext;

    @Test
    void messageStatusRefreshTest() throws Exception {

        AdviceWith.adviceWith(camelContext, "xta-send-port", a -> {
            a.weaveById("send-port").replace().process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {
                    // Not relevant for this test.
                }
            });
        });

        AdviceWith.adviceWith(camelContext, "xta-management-port", a -> {
            a.weaveById("management-port").replace().process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {

                    TransportReport transportReport = new TransportReport();
                    MessageStatusType messageStatusType = new MessageStatusType();
                    messageStatusType.setStatus(BigInteger.valueOf(1));
                    transportReport.setMessageStatus(messageStatusType);
                    exchange.getMessage().setBody(transportReport);
                }
            });
        });

        camelContext.start();

        ClaimImport claimImport = new ClaimImport();
        claimImport.setStorageLocation("storagelocation");
        claimImport.setKassenzeichen("kassenkennzeichen");
        claimImport.setErstellDatum("ddMMyyyy");
        claimImport.setSourceFileName("sourcefile");
        claimImport.setOutputDirectory("outputdirectory");
        claimImport.setOutputFile("outputfile");
        var savedClaimImport = claimImportRepository.save(claimImport);

        Xta xta = new Xta();
        xta.setClaimImportId(savedClaimImport.getId());
        xta.setTransportMessageStatus(0);
        xta.setMessageId("urn:de:xta:messageid:governikusmultimessenger:f7c8e5f2-5527-4d67-b608-b095b861fb9b");

        xtaRepository.save(xta);

        camelContext.createProducerTemplate().sendBody(XtaRouteBuilder.BEPBO_REFRESH_MESSAGE_STATUS, null);

        Xta xtaFound = xtaRepository.findById(xta.getId());

        assertEquals(1, xtaFound.getTransportMessageStatus());
        assertNotNull(xtaFound.getUpdatedAt());

    }

}
