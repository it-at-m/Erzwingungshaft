package de.muenchen.eh.development;

import de.muenchen.eh.Application;
import de.muenchen.eh.TestConstants;
import de.muenchen.eh.XtaTestContext;
import de.muenchen.eh.domain.claim.ClaimRouteBuilder;
import de.muenchen.eh.domain.file.FileImportRouteBuilder;
import de.muenchen.eh.infrastructure.integration.xta.XtaRouteBuilder;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.ExcludeRoutes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//@UseAdviceWith
@SpringBootTest(classes = { Application.class, XtaTestContext.class })
@ExcludeRoutes({ FileImportRouteBuilder.class, ClaimRouteBuilder.class, XtaRouteBuilder.class })
@CamelSpringBootTest
@EnableAutoConfiguration
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_DEVELOPMENT_PROFILE, TestConstants.SPRING_INTEGRATION_PROFILE })
@Disabled(
    "This is not a regular JUnit test. Rather, it can be used as a basis to flexibly configure the various endpoints (db, s3, efile, gmm) and to simulate a test run."
)
public class DevelopmentIdentifierTest {

    /*
     * This test case reads (testdata/in/identifier) and writes to file (testdata/out).
     * Must be configured for example in application-development.yml
     *
     * xjustiz:
     * interface:
     * file:
     * identifier-input: file://testdata/in/identifier?noop=true
     * identifier-ouput: file://testdata/
     *
     */

    @EndpointInject("mock:removeMeEnableDevelopmentMock")
    private MockEndpoint removeMeEnableDevelopmentMock;

    @Test
    void test_identifier() throws InterruptedException {

        removeMeEnableDevelopmentMock.expectedMessageCount(1);

        removeMeEnableDevelopmentMock.assertIsSatisfied();

    }

}
