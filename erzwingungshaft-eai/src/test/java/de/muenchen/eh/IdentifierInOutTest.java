package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.eh.domain.claim.ClaimRouteBuilder;
import de.muenchen.eh.domain.file.FileImportRouteBuilder;
import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import de.muenchen.eh.infrastructure.db.repository.EfileIdentifierRepository;
import de.muenchen.eh.infrastructure.integration.xta.XtaRouteBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.ExcludeRoutes;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@UseAdviceWith
@SpringBootTest(classes = { Application.class, XtaTestContext.class })
@ExcludeRoutes({ FileImportRouteBuilder.class, ClaimRouteBuilder.class, XtaRouteBuilder.class })
@CamelSpringBootTest
@EnableAutoConfiguration
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
public class IdentifierInOutTest extends TestContainerConfiguration {

    @EndpointInject("mock:test-end")
    private MockEndpoint mockTestEnd;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private EfileIdentifierRepository efileIdentifierRepository;

    @Test
    void test_4_imports() throws Exception {

        AdviceWith.adviceWith(camelContext, "rest-openapi-eakte", a -> {
            a.weaveById("openapi-client").before()
                    .process(exchange -> exchange.getMessage().setHeader("TestCase", "IdentifierInOutTest.test_4_imports"));
        });

        AdviceWith.adviceWith(camelContext, "efile-identifier", a -> {
            a.weaveById("identifier-eh-complete").replace().to("mock:test-end");
        });

        // Clean up output file
        Path outputFile = Paths.get("testdata/out/d.kvu.euehpkp0.JHJJMMTT.ein");
        Files.deleteIfExists(outputFile);

        camelContext.start();

        // Start test ...
        mockTestEnd.expectedMessageCount(1);

        uploadToBucketIdentifierTestFileConfiguration(s3InitClient);

        mockTestEnd.assertIsSatisfied(TimeUnit.MINUTES.toMillis(3));
        assertEquals(1, mockTestEnd.getExchanges().size(), "One happy path implemented.");

        assertEquals(4, efileIdentifierRepository.count());

        EfileIdentifier identifier1000020005 = efileIdentifierRepository.findByGeschaeftspartnerId("1000020005").getFirst();
        assertEquals("5793401416631-SKA9512.4-5-0025", identifier1000020005.getIdentifier());
        assertEquals("COO.2150.8819.2.1043389", identifier1000020005.getFileCollectionCooAddress());
        assertEquals("COO.2150.8819.2.1086484", identifier1000020005.getFileCooAddress());
        assertEquals("COO.2150.8819.2.1123247", identifier1000020005.getFineCooAddress());
        assertEquals("Fine added to efile file.", identifier1000020005.getMessage());
        assertEquals("d.kvu.euehpkp0.JHJJMMTT.ein", identifier1000020005.getOutputFileName());
        assertEquals("", identifier1000020005.getComment());

        EfileIdentifier identifier1000020003 = efileIdentifierRepository.findByGeschaeftspartnerId("1000020003").getFirst();
        assertEquals("5793401568639-SKA9512.4-5-0025", identifier1000020003.getIdentifier());
        assertEquals("d.kvu.euehpkp0.JHJJMMTT.ein", identifier1000020003.getOutputFileName());

        EfileIdentifier identifier1000024999 = efileIdentifierRepository.findByGeschaeftspartnerId("1000024999").getFirst();
        assertNull(identifier1000024999.getIdentifier());
        assertEquals("COO.2150.8819.2.1043389", identifier1000024999.getFileCollectionCooAddress());
        assertNull(identifier1000024999.getFileCooAddress());
        assertNull(identifier1000024999.getFineCooAddress());
        assertEquals("EFILE_FILE_NOT_FOUND", identifier1000024999.getMessage());
        assertEquals("File for 'GeschaeftspartnerId' not found in efile.", identifier1000024999.getComment());
        assertNull(identifier1000024999.getOutputFileName());

        EfileIdentifier identifier2000075902 = efileIdentifierRepository.findByGeschaeftspartnerId("2000075902").getFirst();
        assertNull(identifier2000075902.getIdentifier());
        assertNull(identifier2000075902.getFileCollectionCooAddress());
        assertNull(identifier2000075902.getFileCooAddress());
        assertNull(identifier2000075902.getFineCooAddress());
        assertEquals("EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND", identifier2000075902.getMessage());
        assertEquals("Collection file for 'GeschaeftspartnerId' not found in efile.", identifier2000075902.getComment());
        assertNull(identifier2000075902.getOutputFileName());

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(EH_BUCKET_IDENTIFIER_OUTPUT)
                .key("d.kvu.euehpkp0.JHJJMMTT.ein")
                .build();

        Files.createDirectories(Paths.get("testdata/out/"));
        s3InitClient.getObject(getObjectRequest, ResponseTransformer.toFile(outputFile));

        assertTrue(Files.exists(outputFile), "File with new generated identifier not found.");
        List<String> lines = Files.lines(outputFile, StandardCharsets.ISO_8859_1).filter(line -> !line.isBlank()).toList();
        assertEquals(2, lines.size(), "Two lines expected.");
        assertEquals(1, lines.stream().filter(line -> line.contains("5793401416631-SKA9512.4-5-0025")).count(), "Identifier expected.");
        assertEquals(1, lines.stream().filter(line -> line.contains("5793401568639-SKA9512.4-5-0025")).count(), "Identifier expected.");

    }

}
