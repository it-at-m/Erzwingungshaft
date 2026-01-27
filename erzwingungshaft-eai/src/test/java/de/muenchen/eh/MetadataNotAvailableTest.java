package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.db.repository.ClaimImportRepository;
import de.muenchen.eh.db.repository.UnassignableErrorRepository;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@UseAdviceWith
@SpringBootTest(classes = { Application.class }, properties = { "camel.main.java-routes-exclude-pattern=**/XtaRouteBuilder,**/ClaimRouteBuilder" })
@CamelSpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
//@Testcontainers
public class MetadataNotAvailableTest extends TestContainerConfiguration {

    @EndpointInject("mock:test-end")
    private MockEndpoint mockTestEnd;

    @Autowired
    protected ClaimImportRepository claimImportRepository;
    @Autowired
    protected ClaimDocumentRepository claimDocumentRepository;
    @Autowired
    protected ClaimImportLogRepository claimImportLogRepository;
    @Autowired
    protected UnassignableErrorRepository unassignableErrorRepository;

    @Autowired
    protected CamelContext camelContext;

    @Test
    void test_document_not_assignable() throws Exception {

        AdviceWith.adviceWith(camelContext, "import-pdfs", a -> {
            a.weaveById("process-claims").replace().to("mock:test-end");
        });

        camelContext.start();

        // Start test ...
        mockTestEnd.expectedMessageCount(1);

        uploadBucketTestFileConfiguration(s3InitClient);

        mockTestEnd.assertIsSatisfied(TimeUnit.MINUTES.toMillis(3));
        assertEquals(1, mockTestEnd.getExchanges().size(), "One happy path implemented.");

        // Database
        assertEquals(5, claimImportRepository.count(), "5 imports expected.");

        List<ClaimDocument> claimDocuments = claimDocumentRepository.findByDocumentType("Antrag");
        assertEquals(1, claimDocuments.size(), "1 claim document Antrag expected 1000013749");
        assertEquals(7, claimImportLogRepository.count(), "7 claim import logs expected.");
        assertEquals(5, claimImportLogRepository.findByMessage("IMPORT_DATA_FILE_CREATED").size(),
                "D.KVU.EUDG0P0.20240807.EZH contains 5 lines to import.");
        assertEquals(1, claimImportLogRepository.findByMessage("IMPORT_ANTRAG_IMPORT_DIRECTORY").size(),
                "1 claims contains ANTRAG to import.");
        assertEquals(1, claimImportLogRepository.findByMessage("IMPORT_ANTRAG_IMPORT_DB").size(),
                "1 claims contains ANTRAG to import in db.");

        assertEquals(1, unassignableErrorRepository.count(), "1 unassignable error expected.");

        // S3 buckets
        assertEquals(0, s3BucketObjectCount(EH_BUCKET_ANTRAG, s3InitClient), "Claim import bucket should be empty.");
        assertEquals(0, s3BucketObjectCount(EH_BUCKET_PDF, s3InitClient), "Pdf import bucket should be empty.");
        assertEquals(7, s3BucketObjectCount(EH_BUCKET_BACKUP, s3InitClient), "7 backup files expected.");

    }

    public static void uploadBucketTestFileConfiguration(S3Client s3InitClient) {

        // Initialize S3
        s3InitClient.putObject(PutObjectRequest.builder().bucket(EH_BUCKET_ANTRAG).key(METADATA).build(),
                Path.of(new File("testdata/in/metadata/D.KVU.EUDG0P0.20240807.EZH").toURI()));

        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000013749_5793303492524_20240807_EH.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000013749_5793303492524_20240807_EH.pdf").toURI()));

        // URB PDF without metadata ...20240807  -->  ...20230807
        s3InitClient.putObject(
                PutObjectRequest.builder().bucket(EH_BUCKET_PDF).key("1000013749_5793303492524_20230807_URB.pdf")
                        .build(),
                Path.of(new File("testdata/in/pdf/1000013749_5793303492524_20240807_URB.pdf").toURI()));

    }

}
