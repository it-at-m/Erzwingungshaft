package de.muenchen.eh;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Testcontainers
public class TestContainerConfiguration extends TestHelper {

    protected static final String EH_BUCKET_BACKUP = "eh-backup";
    protected static final String EH_BUCKET_PDF = "eh-import-pdf";
    protected static final String EH_BUCKET_ANTRAG = "eh-import-antrag";

    protected static final String METADATA = "D.KVU.EUDG0P0.20240807.EZH";

    private WireMockServer wireMockContainer;

    @Container
    protected static MinIOContainer minioContainer = new MinIOContainer(DockerImageName.parse("minio/minio:latest"))
            .withExposedPorts(9000);

    static {
        minioContainer.setPortBindings(List.of("9000:9000"));
    }

    @DynamicPropertySource
    protected static void overrideProperties(DynamicPropertyRegistry registry) {
        // S3-Minio
        registry.add("spring.minio.url", minioContainer::getContainerIpAddress);
        registry.add("spring.minio.access-key", minioContainer::getUserName);
        registry.add("spring.minio.secret-key", minioContainer::getPassword);

        // WireMockServer
        registry.add("server.port", () -> 8081);
    }

    protected static S3Client s3InitClient;

    @BeforeEach
    protected void setUp() throws URISyntaxException {

        wireMockContainer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().port(8081).withRootDirectory("../stack/wiremock"));
        wireMockContainer.start();

        // Test Setup
        s3InitClient = S3Client.builder().endpointOverride(new URI("http://127.0.0.1:9000")).region(Region.of("local"))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(minioContainer.getUserName(), minioContainer.getPassword())))
                .build();

        initializeS3Bucket();

    }

    @AfterEach
    protected void teardown() {
        wireMockContainer.stop();
        minioContainer.stop();
    }

    protected int s3BucketObjectCount(String bucketName, S3Client s3InitClient) {
        return s3InitClient.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build()).contents().size();
    }

    protected void initializeS3Bucket() {
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
        s3InitClient.createBucket(CreateBucketRequest.builder().bucket(EH_BUCKET_BACKUP).build());
        s3InitClient.createBucket(CreateBucketRequest.builder().bucket(EH_BUCKET_PDF).build());
    }

}
