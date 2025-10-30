package de.muenchen.eh;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.engine.DefaultInflightRepository;
import org.apache.camel.model.RouteDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "batch", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApplicationBatchRunner implements CommandLineRunner {

    private final ApplicationContext springContext;
    private final CamelContext camelContext;
    private final ConfigurableApplicationContext appContext;

    public ApplicationBatchRunner(ApplicationContext springContext, CamelContext camelContext, ConfigurableApplicationContext appContext) {
        this.springContext = springContext;
        this.camelContext = camelContext;
        this.appContext = appContext;
    }

    @Override
    public void run(String... args) throws Exception {

        springContext.getBeansOfType(RouteBuilder.class)
                .values()
                .forEach(rb -> {
                    try {
                        camelContext.addRoutes(rb);
                        log.info("Add route builder : {}", rb.getClass().getSimpleName());
                        List<RouteDefinition> routes = rb.getRoutes().getRoutes();
                        routes.forEach(route -> {
                            log.info("   add route : {}", route.getRouteId());
                        });

                    } catch (Exception e) {
                        throw new RuntimeException("Error adding route : " + rb, e);
                    }
                });

        camelContext.start();
        log.info("CamelContext started. Looking for files to import ...");

        waitUntilIdle();

        log.info("... all imported files are done.");
        camelContext.stop();

        log.info("Application terminates.");
        appContext.close();
    }

    private void waitUntilIdle() throws InterruptedException {
        DefaultInflightRepository inflight = (DefaultInflightRepository) camelContext.getInflightRepository();

        int idleCount = 0;
        while (true) {
            int inflightCount = inflight.size();
            if (inflightCount == 0) {
                idleCount++;
            } else {
                idleCount = 0;
            }

            if (idleCount > 3) break;

            Thread.sleep(1000);
        }
    }

}
