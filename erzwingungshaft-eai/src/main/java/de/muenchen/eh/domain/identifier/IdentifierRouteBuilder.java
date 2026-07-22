package de.muenchen.eh.domain.identifier;

import java.nio.charset.StandardCharsets;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdentifierRouteBuilder extends RouteBuilder {

    @Value("${xjustiz.interface.file.line-break}")
    protected String lineBreak;

    public static final String PSCD_IDENTIFIER_CONTENT_UNMARSHALL = "direct:pscdIdentifierContentUnmarshall";
    public static final String PSCD_OUTPUT_MARSHALL = "direct:pscdOutputMarshall";
    public static final String IDENTIFIER_FINE = "direct:identifierFine";
    public static final String S3_OUTPUT_UPLOAD = "direct:s3OutputUpload";

    @Override
    public void configure() throws Exception {

        // spotless:off
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception}")
                .bean("logServiceIdentifier", "logError");

        from("{{xjustiz.interface.file.identifier-input}}").routeId("efile-identifier")
                .convertBodyTo(String.class, StandardCharsets.ISO_8859_1.name())
                .split(body().tokenize(lineBreak)).aggregationStrategy(new GroupedBodyAggregationStrategy())
                    .process("importPscdDataEnricher")
                    .process("identifierCreator")
                .end()
                .process("identifierOutputContent")
                .log(LoggingLevel.DEBUG, "identifier-eh-process completed.'.").id("identifier-eh-complete")
                .stop();

        from(PSCD_IDENTIFIER_CONTENT_UNMARSHALL).routeId("pscd-data-unmarshall")
                .unmarshal().bindy(BindyType.Fixed, PscdDataImport.class);

        from(PSCD_OUTPUT_MARSHALL).routeId("pscd-output-marshall")
                .marshal().bindy(BindyType.Fixed, PscdDataExport.class);

        from(IDENTIFIER_FINE).routeId("identifier-fine")
                .process("eAPLIdentifierExecutor");

        from(S3_OUTPUT_UPLOAD).routeId("s3-output")
                .toD("{{xjustiz.interface.file.identifier-output}}")
                .to("mock:removeMeEnableDevelopmentMock");

        // spotless:on

    }
}
