package de.muenchen.eh;

import de.muenchen.eh.xta.camel.XtaContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {Application.class, XtaContext.class}, properties = {"camel.springboot.java-routes-include-pattern=**/XtaRouteBuilder,**/BaseRouteBuilder" })
@TestPropertySource(locations = { "classpath:application-test.yml" }, properties = "debug=false")
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
public class BepboTest {

    @Test
    public void bepboTest() {
        assertTrue(true);
    }

}
