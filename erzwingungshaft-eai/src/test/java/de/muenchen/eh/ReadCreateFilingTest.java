package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.eh.common.XmlUnmarshaller;
import de.muenchen.eh.db.entity.Claim;
import de.muenchen.eh.db.entity.ClaimEfile;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.entity.ClaimImportLog;
import de.muenchen.eh.db.entity.ClaimLog;
import de.muenchen.eh.db.entity.ClaimXml;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimContentRepository;
import de.muenchen.eh.db.repository.ClaimDataRepository;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.db.repository.ClaimImportRepository;
import de.muenchen.eh.db.repository.ClaimLogRepository;
import de.muenchen.eh.db.repository.ClaimRepository;
import de.muenchen.eh.db.repository.ClaimXmlRepository;
import de.muenchen.eh.db.repository.XtaRepository;
import de.muenchen.eh.db.service.ClaimService;
import de.muenchen.xjustiz.generated.xjustiz0500straf35.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import de.xoev.transport.xta._211.MessageStatusType;
import de.xoev.transport.xta._211.TransportReport;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@UseAdviceWith
@SpringBootTest(classes = { Application.class, XtaTestContext.class })
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
public class ReadCreateFilingTest extends TestContainerConfiguration {

    @EndpointInject("mock:test-end")
    private MockEndpoint mockTestEnd;

    @Autowired
    protected ClaimImportRepository claimImportRepository;
    @Autowired
    protected ClaimRepository claimRepository;
    @Autowired
    protected ClaimDocumentRepository claimDocumentRepository;
    @Autowired
    protected ClaimContentRepository claimContentRepository;
    @Autowired
    protected ClaimDataRepository claimDataRepository;
    @Autowired
    protected ClaimXmlRepository claimlXmlRepository;
    @Autowired
    protected ClaimEfileRepository claimEfileRepository;
    @Autowired
    protected ClaimImportLogRepository claimImportLogRepository;
    @Autowired
    protected ClaimLogRepository claimLogRepository;
    @Autowired
    protected XtaRepository xtaRepository;

    @Autowired
    private ClaimService claimService;

    @Autowired
    protected CamelContext camelContext;

    @Test
    void test_5_claims() throws Exception {

        AdviceWith.adviceWith(camelContext, "claim-eh-process", a -> {
            a.weaveById("claim-eh-process-gpid").replace().to("mock:test-end");
        });

        AdviceWith.adviceWith(camelContext, "xta-management-port", a -> {
            a.weaveById("management-port").replace().process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {

                    if (exchange.getMessage().getHeader(CxfConstants.OPERATION_NAME, String.class)
                            .equals("createMessageId")) {
                        AttributedURIType attrUriType = new AttributedURIType();
                        attrUriType.setValue("111-222-333-444");
                        exchange.getMessage().setBody(attrUriType);
                    } else { // OperationName == getTransportReport
                        TransportReport transportReport = new TransportReport();
                        MessageStatusType messageStatusType = new MessageStatusType();
                        messageStatusType.setStatus(BigInteger.valueOf(1));
                        transportReport.setMessageStatus(messageStatusType);
                        exchange.getMessage().setBody(transportReport);
                    }
                }
            });
        });

        AdviceWith.adviceWith(camelContext, "xta-send-port", a -> {
            a.weaveById("send-port").replace().process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                }
            });
        });

        camelContext.start();

        // Start test ...
        mockTestEnd.expectedMessageCount(1);

        uploadBucketTestFileConfiguration(s3InitClient);

        mockTestEnd.assertIsSatisfied(TimeUnit.MINUTES.toMillis(3));
        assertEquals(1, mockTestEnd.getExchanges().size(), "One happy path implemented.");

        // Database
        assertEquals(5, claimImportRepository.count(), "5 imports expected.");
        assertEquals(3, claimRepository.count(),
                "3 claims expected (gp_id : 1000809085/5793341761427, 1000013749, 1000258309).");
        assertEquals(6, claimDocumentRepository.count(),
                "6 claim documents expected. 2 (Antrag, Urbescheid) for each gp_id : 1000809085, 1000013749, 1000258309");
        assertEquals(3, claimContentRepository.count(),
                "3 claim contents expected (gp_id : 1000809085/5793341761427, 1000013749, 1000258309).");
        assertEquals(3, claimDataRepository.count(),
                "3 claim data expected (gp_id : 1000809085/5793341761427, 1000013749, 1000258309).");
        assertEquals(3, claimlXmlRepository.count(),
                "3 claim xml expected (gp_id : 1000809085/5793341761427, 1000013749, 1000258309).");
        assertEquals(1, claimEfileRepository.count(), "1 claim efile expected (gp_id : 1000013749).");
        assertEquals(17, claimImportLogRepository.count(), "17 claim import logs expected.");
        assertEquals(5, claimImportLogRepository.findByMessage("IMPORT_DATA_FILE_CREATED").size(),
                "D.KVU.EUDG0P0.20240807.EZH contains 5 lines to import.");
        assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_ANTRAG_IMPORT_DIRECTORY").size(),
                "3 claims contains ANTRAG to import.");
        assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_BESCHEID_IMPORT_DIRECTORY").size(),
                "3 claims contains BESCHEID to import.");
        assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_ANTRAG_IMPORT_DB").size(),
                "3 claims contains ANTRAG to import in db.");
        assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_BESCHEID_IMPORT_DB").size(),
                "3 claims contains BESCHEID to import in db.");
        assertEquals(31, claimLogRepository.count(), "31 claim logs expected.");
        assertEquals(28, claimLogRepository.findByMessageTyp(MessageType.INFO).size(), "28 import INFO expected.");
        assertEquals(1, claimLogRepository.findByMessageTyp(MessageType.WARN).size(), "1 import WARN expected.");
        assertEquals(2, claimLogRepository.findByMessageTyp(MessageType.ERROR).size(), "2 import ERROR expected.");
        assertEquals(1, xtaRepository.count(), "1 send message expected.");

        List<Claim> claims = claimService.claimEfilesWithCorrespondingGId("1000013749");
        ClaimEfile claimEfile = claims.get(0).getClaimEfile();
        assertEquals("COO.2150.9169.1.1605576", claimEfile.getCollection());
        assertEquals("COO.2150.9169.1.1957003", claimEfile.getFile());
        assertEquals("COO.2150.9169.1.2119717", claimEfile.getFine());
        assertEquals("COO.2150.9169.1.2119719", claimEfile.getOutgoing());
        assertEquals("COO.2150.9169.1.2119720", claimEfile.getAntragDocument());
        assertEquals("COO.2150.9169.1.2119721", claimEfile.getBescheidDocument());
        assertEquals("COO.2150.9169.1.2119722", claimEfile.getXml());

        // S3 buckets
        assertEquals(0, s3BucketObjectCount(EH_BUCKET_ANTRAG, s3InitClient), "Claim import bucket should be empty.");
        assertEquals(0, s3BucketObjectCount(EH_BUCKET_PDF, s3InitClient), "Pdf import bucket should be empty.");
        assertEquals(11, s3BucketObjectCount(EH_BUCKET_BACKUP, s3InitClient), "11 backup files expected.");

        // XML message
        List<ClaimImport> list_claimImport_1000013749_5793303492524 = claimImportRepository.findByGeschaeftspartnerIdAndKassenzeichen("1000013749",
                "5793303492524");
        assertEquals(1, list_claimImport_1000013749_5793303492524.size(), "1 claim import expected.");
        Claim claim_1000013749_5793303492524 = claimRepository.findByClaimImportId(list_claimImport_1000013749_5793303492524.getFirst().getId());
        List<ClaimXml> claimXml_1000013749_5793303492524 = claimlXmlRepository.findByClaimId(claim_1000013749_5793303492524.getId());
        assertEquals(1, claimXml_1000013749_5793303492524.size(), "1 claim xml expected.");
        String xJustizXml = claimXml_1000013749_5793303492524.getFirst().getContent();
        assertFalse(
                testXmlCompare(Files.readString(Paths.get("src/test/resources/Compare_Reference_1000013749_5793303492524_20240807.txt")),
                        ProcessXmlDocumentCompare.process(xJustizXml)),
                "All xml elements with dynamic content have been removed. The content should be the same.");

        NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 lastXJustizMessage = XmlUnmarshaller
                .unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(xJustizXml);

        var betroffener = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligung().getFirst()
                .getBeteiligter().getAuswahlBeteiligter().getNatuerlichePerson();

        assertEquals("Test", betroffener.getVollerName().getNachname());
        assertEquals("EXXXX", betroffener.getVollerName().getVorname());

        var beteiligung = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligung().getLast();
        assertEquals("046", beteiligung.getRolle().getFirst().getRollenbezeichnung().getCode());

        assertEquals("Stadt München",
                lastXJustizMessage.getNachrichtenkopf().getAuswahlAbsender().getAbsenderSonstige());

        List<ClaimImport> claimImports = claimImportRepository
                .findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrueOrderByIdAsc();

        // DB log 1000809085_5793341761427
        Optional<ClaimImport> first_claimImport_1000809085_5793341761427 = claimImports.stream()
                .filter(ci -> ci.getGeschaeftspartnerId().equals("1000809085")).findFirst();
        var claimImport_1000809085_5793341761427 = first_claimImport_1000809085_5793341761427
                .orElseThrow(() -> new AssertionError("ClaimImport for geschaeftspartnerId 1000809085 not found"));

        assertEquals("1000809085", claimImport_1000809085_5793341761427.getGeschaeftspartnerId());
        assertEquals("5793341761427", claimImport_1000809085_5793341761427.getKassenzeichen());

        List<ClaimImportLog> infoClaimImportLogs = claimImportLogRepository
                .findByClaimImportIdAndMessageType(claimImport_1000809085_5793341761427.getId(), MessageType.INFO);
        assertEquals(5, infoClaimImportLogs.size());
        assertEquals(0, claimImportLogRepository
                .findByClaimImportIdAndMessageType(claimImport_1000809085_5793341761427.getId(), MessageType.ERROR)
                .size(), "No errors expected.");

        Claim claim_1000809085_5793341761427 = claimRepository
                .findByClaimImportId(claimImport_1000809085_5793341761427.getId());
        List<ClaimLog> infoClaimLogs = claimLogRepository
                .findByClaimIdAndMessageTyp(claim_1000809085_5793341761427.getId(), MessageType.INFO);
        assertEquals(6, infoClaimLogs.size());

        assertNotNull(claim_1000809085_5793341761427.getEhUuid(),
                "With the xml generation a uuid is created which is persisted in db.");
        var claimlog_errors_1000809085_5793341761427 = claimLogRepository
                .findByClaimIdAndMessageTyp(claim_1000809085_5793341761427.getId(), MessageType.ERROR);
        assertEquals(1, claimlog_errors_1000809085_5793341761427.size(), "One error expected.");
        assertEquals("EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND",
                claimlog_errors_1000809085_5793341761427.getFirst().getMessage());

    }

    public static void uploadBucketTestFileConfiguration(S3Client s3InitClient) {

        // Initialize S3
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_ANTRAG).key(METADATA).build(),
                Path.of(new File("testdata/in/metadata/D.KVU.EUDG0P0.20240807.EZH").toURI()));

        // Not assignable to 'Einzelakte'
        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000809085_5793341761427_20240807_EH.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000809085_5793341761427_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000809085_5793341761427_20240807_URB.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000809085_5793341761427_20240807_URB.pdf").toURI()));

        // Assignable to 'Einzelakte'
        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000013749_5793303492524_20240807_EH.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000013749_5793303492524_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000013749_5793303492524_20240807_URB.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000013749_5793303492524_20240807_URB.pdf").toURI()));

        // IllegalArgumentException : The mandatory field defined at the position 31
        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000258309_5793402494421_20240807_EH.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000258309_5793402494421_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000258309_5793402494421_20240807_URB.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000258309_5793402494421_20240807_URB.pdf").toURI()));

    }

}
