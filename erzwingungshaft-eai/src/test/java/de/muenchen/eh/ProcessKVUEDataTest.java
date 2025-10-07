package de.muenchen.eh;

import de.muenchen.eh.common.XmlUnmarshaller;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.log.db.entity.*;
import de.muenchen.eh.log.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.log.db.repository.ClaimImportRepository;
import de.muenchen.eh.log.db.repository.ClaimLogRepository;
import de.muenchen.eh.log.db.repository.ClaimRepository;
import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class ProcessKVUEDataTest {

    @EndpointInject("mock:xjustizMessage")
    private MockEndpoint xjustizMsg;

    @EndpointInject("mock:error")
    private MockEndpoint failures;

    @Autowired
    private ClaimImportRepository claimImportRepository;

    @Autowired
    private ClaimImportLogRepository claimImportLogRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimLogRepository claimLogRepository;

    private static final String EH_BUCKET_IMPORT = "eh-backup";
    private static final String EH_BUCKET_PDF = "eh-import-pdf";
    private static final String EH_BUCKET_ANTRAG = "eh-import-antrag";

    private static final String METADATA = "D.KVU.EUDG0P0.20240807.EZH";

    private static S3Client s3InitClient;

    @BeforeEach
    public void setUp() throws URISyntaxException {

        s3InitClient = S3Client.builder().endpointOverride(new URI("http://127.0.0.1:9000")).region(Region.of("local"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("minio", "Test1234"))).build();

        // Remove old test content
        var bucketsInTest = s3InitClient.listBuckets();

        // Remove bucket objects
        bucketsInTest.buckets().forEach(b -> {
            var content = s3InitClient.listObjects(ListObjectsRequest.builder().bucket(b.name()).build());
            content.contents().forEach(o -> {
                s3InitClient.deleteObject(DeleteObjectRequest.builder().bucket(b.name()).key(o.key()).build());
            });
        });
        // Delete buckets
        bucketsInTest.buckets().forEach(b -> {
            s3InitClient.deleteBucket(DeleteBucketRequest.builder().bucket(b.name()).build());
        });

        // Create test bucket
        s3InitClient.createBucket(CreateBucketRequest.builder().bucket(EH_BUCKET_ANTRAG).build());
        s3InitClient.createBucket(CreateBucketRequest.builder().bucket(EH_BUCKET_IMPORT).build());
        s3InitClient.createBucket(CreateBucketRequest.builder().bucket(EH_BUCKET_PDF).build());
    }

    @Test
    void test_readDataAndCreateXjustizXml() throws Exception {

        uploadBucketTestFileConfiguration();

        // Start test ...
        xjustizMsg.expectedMessageCount(1);
        xjustizMsg.assertIsSatisfied(TimeUnit.SECONDS.toMillis(5));

        failures.expectedMessageCount(0);
        failures.assertIsSatisfied(TimeUnit.SECONDS.toMillis(5));

        assertEquals(1, xjustizMsg.getExchanges().size(), "One happy path implemented.");
        ClaimProcessingContentWrapper dataWrapper = xjustizMsg.getExchanges().getFirst().getMessage().getBody(ClaimProcessingContentWrapper.class);

        // XML message
        NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 lastXJustizMessage = XmlUnmarshaller.unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(dataWrapper.getXjustizXml());

        var betroffener = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getFirst().getBeteiligter().getAuswahlBeteiligter().getNatuerlichePerson();

        assertEquals("Test", betroffener.getVollerName().getNachname());
        assertEquals("EXXXX", betroffener.getVollerName().getVorname());

        var beteiligung = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getLast();
        assertEquals("046", beteiligung.getRolles().getFirst().getRollenbezeichnung().getCode());

        assertEquals("Stadt München",lastXJustizMessage.getNachrichtenkopf().getAuswahlAbsender().getAbsenderSonstige());

        List<ClaimImport> claimImports  = claimImportRepository.findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrueOrderByIdAsc();

        // DB log 1000809085_5793341761427
        Optional<ClaimImport> first_claimImport_1000809085_5793341761427 = claimImports.stream().filter(ci -> ci.getGeschaeftspartnerId().equals("1000809085")).findFirst();
        var claimImport_1000809085_5793341761427 = first_claimImport_1000809085_5793341761427.orElseThrow();
        assertEquals("1000809085", claimImport_1000809085_5793341761427.getGeschaeftspartnerId());
        assertEquals("5793341761427", claimImport_1000809085_5793341761427.getKassenzeichen());

        List<ClaimImportLog> infoClaimImportLogs = claimImportLogRepository.findByClaimImportIdAndMessageTyp(claimImport_1000809085_5793341761427.getId(), MessageType.INFO);
        assertEquals(5, infoClaimImportLogs.size());
        assertEquals(0, claimImportLogRepository.findByClaimImportIdAndMessageTyp(claimImport_1000809085_5793341761427.getId(), MessageType.ERROR).size(), "No errors expected.");

        Claim claim_1000809085_5793341761427 = claimRepository.findByClaimImportId(claimImport_1000809085_5793341761427.getId());
        List<ClaimLog> infoClaimLogs = claimLogRepository.findByClaimIdAndMessageTyp(claim_1000809085_5793341761427.getId(), MessageType.INFO);
        assertEquals(6, infoClaimLogs.size());

        assertNotNull(claim_1000809085_5793341761427.getEhUuid(), "With the xml generation a uuid is created which is persisted in db.");
        var claimlog_errors_1000809085_5793341761427 =  claimLogRepository.findByClaimIdAndMessageTyp(claim_1000809085_5793341761427.getId(), MessageType.ERROR);
        assertEquals(1, claimlog_errors_1000809085_5793341761427.size(), "One error expected.");
        assertEquals("GESCHAEFTSPARTNERID_EINZELKAKTE_NOT_FOUND", claimlog_errors_1000809085_5793341761427.getFirst().getMessage());

        // DB log 1000258309_5793402494421
        Optional<ClaimImport> first_claimImport_1000258309_5793402494421 = claimImports.stream().filter(ci -> ci.getGeschaeftspartnerId().equals("1000258309")).findFirst();
        var claimImport_1000258309_5793402494421 = first_claimImport_1000258309_5793402494421.orElseThrow();
        Claim claim_1000258309_5793402494421 = claimRepository.findByClaimImportId(claimImport_1000258309_5793402494421.getId());
        var claimlog_errors_1000258309_5793402494421 =  claimLogRepository.findByClaimIdAndMessageTyp(claim_1000258309_5793402494421.getId(), MessageType.ERROR);
        assertEquals(1, claimlog_errors_1000258309_5793402494421.size(), "One error expected.");
        assertEquals("The mandatory field defined at the position 31 (de.muenchen.eh.kvue.claim.ImportClaimData.ehtatstdb) is empty for the line: 1", claimlog_errors_1000258309_5793402494421.getFirst().getMessage());


    }

    private static void uploadBucketTestFileConfiguration() {

        // Initialize S3
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_ANTRAG).key(METADATA).build(),
                Path.of(new File("testdata/in/metadata/D.KVU.EUDG0P0.20240807.EZH").toURI()));

        // Not assignable to 'Einzelakte'
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000809085_5793341761427_20240807_EH.pdf").build(),
                Path.of(new File("testdata/in/pdf/1000809085_5793341761427_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000809085_5793341761427_20240807_URB.pdf").build(),
                Path.of(new File("testdata/in/pdf/1000809085_5793341761427_20240807_URB.pdf").toURI()));

        // Assignable to 'Einzelakte'
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000013749_5793303492524_20240807_EH.pdf").build(),
                Path.of(new File("testdata/in/pdf/1000013749_5793303492524_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000013749_5793303492524_20240807_URB.pdf").build(),
                Path.of(new File("testdata/in/pdf/1000013749_5793303492524_20240807_URB.pdf").toURI()));

        // IllegalArgumentException : The mandatory field defined at the position 31
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000258309_5793402494421_20240807_EH.pdf").build(),
                Path.of(new File("testdata/in/pdf/1000258309_5793402494421_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000258309_5793402494421_20240807_URB.pdf").build(),
                Path.of(new File("testdata/in/pdf/1000258309_5793402494421_20240807_URB.pdf").toURI()));

    }

}
