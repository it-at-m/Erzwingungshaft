package de.muenchen.eh.development;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.muenchen.eh.Application;
import de.muenchen.eh.ReadCreateFilingTest;
import de.muenchen.eh.TestConstants;
import de.muenchen.eh.XtaTestContext;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;

@UseAdviceWith
@SpringBootTest(classes = { Application.class, XtaTestContext.class })
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_INTEGRATION_PROFILE, TestConstants.SPRING_DEVELOPMENT_PROFILE })
@Disabled("The tests are less junit tests and more bebpo integration tests to try something out.")
class DevelopmentReadCreateFilingTest {	

    @EndpointInject("mock:finish")
    private MockEndpoint finish;

    @Autowired
	private ClaimImportRepository claimImportRepository;
	@Autowired
	private ClaimRepository claimRepository;
	@Autowired
	private ClaimDocumentRepository claimDocumentRepository;
	@Autowired
	private ClaimContentRepository claimContentRepository;
	@Autowired
	private ClaimDataRepository claimDataRepository;
	@Autowired
	private ClaimXmlRepository claimlXmlRepository;
	@Autowired
	private ClaimEfileRepository claimEfileRepository;
	@Autowired
	private ClaimImportLogRepository claimImportLogRepository;
	@Autowired
	private ClaimLogRepository claimLogRepository;
	
    @Autowired
    CamelContext camelContext;

    private static final String EH_BUCKET_IMPORT = "eh-backup";
    private static final String EH_BUCKET_PDF = "eh-import-pdf";
    private static final String EH_BUCKET_ANTRAG = "eh-import-antrag";
    
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
    void test_5_claims() throws Exception {

        AdviceWith.adviceWith(camelContext, "claim-eh-process", a -> {
            a.weaveById("bebpoService")
                    .replace()
                    .to("mock:finish");
        });

        /*
         * In case bebpo/xta connection should not be mocked.
         * Comment out AdviceWith and add the mock in ClaimRouteBuilder.
         *
         * ClaimRouteBuilder
         * ...
         * .process("{{xjustiz.interface.xta}}").id("bebpoService")
         * .to("mock:finish")
         * .end()
         * ...
         */

        camelContext.start();

        // Start test ...
        finish.expectedMessageCount(1);

        ReadCreateFilingTest.uploadBucketTestFileConfiguration(s3InitClient);

        finish.assertIsSatisfied(TimeUnit.MINUTES.toMillis(2));

        assertEquals(1, finish.getExchanges().size(), "One happy path implemented.");
 
        // Database
   		assertEquals(5, claimImportRepository.count(), "5 imports expected.");
     	assertEquals(3, claimRepository.count(), "3 claims expected (gp_id : 1000809085/5793341761427, 1000013749, 1000258309).");
     	assertEquals(6, claimDocumentRepository.count(), "6 claim documents expected. 2 (Antrag, Urbescheid) for each gp_id : 1000809085, 1000013749, 1000258309");
     	assertEquals(2, claimContentRepository.count(), "2 claim contents expected (gp_id : 1000809085/5793341761427, 1000013749).");
     	assertEquals(2, claimDataRepository.count(), "2 claim data expected (gp_id : 1000809085/5793341761427, 1000013749).");
     	assertEquals(2, claimlXmlRepository.count(), "2 claim xml expected (gp_id : 1000809085/5793341761427, 1000013749).");
     	assertEquals(1, claimEfileRepository.count(), "1 claim efile expected (gp_id : 1000013749).");
     	assertEquals(17, claimImportLogRepository.count(), "17 claim import logs expected.");
     	assertEquals(5, claimImportLogRepository.findByMessage("IMPORT_DATA_FILE_CREATED").size(), "D.KVU.EUDG0P0.20240807.EZH contains 5 lines to import.");
     	assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_ANTRAG_IMPORT_DIRECTORY").size(), "3 claims contains ANTRAG to import.");
     	assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_BESCHEID_IMPORT_DIRECTORY").size(), "3 claims contains BESCHEID to import.");
     	assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_ANTRAG_IMPORT_DB").size(), "3 claims contains ANTRAG to import in db.");
     	assertEquals(3, claimImportLogRepository.findByMessage("IMPORT_BESCHEID_IMPORT_DB").size(), "3 claims contains BESCHEID to import in db.");
     	assertEquals(24, claimLogRepository.count(), "24 claim logs expected.");
     	assertEquals(21, claimLogRepository.findByMessageTyp(MessageType.INFO).size(), "21 import INFO expected.");
     	assertEquals(1, claimLogRepository.findByMessageTyp(MessageType.WARN).size(), "1 import WARN expected.");
     	assertEquals(2, claimLogRepository.findByMessageTyp(MessageType.ERROR).size(), "2 import ERROR expected.");
        
    }
    

}
