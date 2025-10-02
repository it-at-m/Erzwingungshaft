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
import java.util.Iterator;
import java.util.List;
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
    private static final String ANTRAG = "1000809085_5793341761427_20240807_EH.pdf";
    private static final String URBESCHEID = "1000809085_5793341761427_20240807_URB.pdf";

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

        ClaimProcessingContentWrapper dataWrapper = xjustizMsg.getExchanges().getLast().getMessage().getBody(ClaimProcessingContentWrapper.class);

        // XML message
        NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 lastXJustizMessage = XmlUnmarshaller.unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(dataWrapper.getXjustizXml());

        var betroffener = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getFirst().getBeteiligter().getAuswahlBeteiligter().getNatuerlichePerson();

        assertEquals("TESTT", betroffener.getVollerName().getNachname());
        assertEquals("EXXXX", betroffener.getVollerName().getVorname());

        var beteiligung = lastXJustizMessage.getGrunddaten().getVerfahrensdaten().getBeteiligungs().getLast();
        assertEquals("046", beteiligung.getRolles().getFirst().getRollenbezeichnung().getCode());

        assertEquals("Stadt München",lastXJustizMessage.getNachrichtenkopf().getAuswahlAbsender().getAbsenderSonstige());

        // DB logging
        ClaimImport claimImport_1000809085_5793341761427  = claimImportRepository.findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrueOrderByIdAsc().getLast();
        assertEquals("1000809085", claimImport_1000809085_5793341761427.getGeschaeftspartnerId());
        assertEquals("5793341761427", claimImport_1000809085_5793341761427.getKassenzeichen());

        List<ClaimImportLog> infoClaimImportLogs = claimImportLogRepository.findByClaimImportIdAndMessageTyp(claimImport_1000809085_5793341761427.getId(), MessageType.INFO);
        assertEquals(5, infoClaimImportLogs.size());
        assertEquals(0, claimImportLogRepository.findByClaimImportIdAndMessageTyp(claimImport_1000809085_5793341761427.getId(), MessageType.ERROR).size(), "No errors expected.");

        Claim claim = claimRepository.findByClaimImportId(claimImport_1000809085_5793341761427.getId());
        List<ClaimLog> infoClaimLogs = claimLogRepository.findByClaimIdAndMessageTyp(claim.getId(), MessageType.INFO);
        assertEquals(6, infoClaimLogs.size());

        assertNotNull(claim.getEhUuid(), "With the xml generation a uuid is created which is persisted in db.");
        assertEquals(0, claimLogRepository.findByClaimIdAndMessageTyp(claim.getId(), MessageType.ERROR).size(), "No errors expected.");


    }

    private static void uploadBucketTestFileConfiguration() {

        // Initialize S3
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_ANTRAG).key(METADATA).build(),
                Path.of(new File("testdata/in/metadata/D.KVU.EUDG0P0.20240807.EZH").toURI()));

        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key(ANTRAG).build(),
                Path.of(new File("testdata/in/pdf/1000809085_5793341761427_20240807_EH.pdf").toURI()));

        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key(URBESCHEID).build(),
                Path.of(new File("testdata/in/pdf/1000809085_5793341761427_20240807_URB.pdf").toURI()));
    }

}
