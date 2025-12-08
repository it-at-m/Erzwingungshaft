package de.muenchen.eh.file;

import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.claim.ClaimRouteBuilder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileImportRouteBuilder extends BaseRouteBuilder {

    private static final String BUCKET_NAME = "bucket-name";

    public static final String CLAIM_IMPORT_DATA_UNMARSHALL = "direct:claimImportDataUnmarshall";
    public static final String S3_UPLOAD = "direct:s3upload";

    @Override
    public void configure() {

        super.configure();

        from("{{xjustiz.interface.file.consume}}")
                .routeId("generate-import-files")
                .convertBodyTo(String.class, StandardCharsets.ISO_8859_1.name())
                .split(body().tokenize(lineBreak), new GroupedBodyAggregationStrategy())
                .process("importDataEnricher")
                .process("claimMetadataFile")
                .bean("logServiceImport", "logClaimImport")
                .end()
                .log(LoggingLevel.DEBUG, "de.muenchen.eh", "'${body.size}' claims imported.")
                .process(exchange -> {
                    exchange.getContext().getRouteController().startRoute("import-pdfs");
                });

        from(CLAIM_IMPORT_DATA_UNMARSHALL).routeId("import-data-unmarshall")
                .unmarshal().bindy(BindyType.Fixed, ImportClaimIdentifierData.class);

        from(S3_UPLOAD).routeId("s3-upload")
                .to("{{xjustiz.interface.file.file-output}}");

        from("{{xjustiz.interface.pdf.consume}}")
                .routeId("import-pdfs")
                .autoStartup(false)
                .idempotentConsumer(simple("${header.CamelAwsS3Key}"), MemoryIdempotentRepository.memoryIdempotentRepository(1000))
                .process(new S3ObjectName())
                .toD("{{xjustiz.interface.pdf.file-output}}")
                .bean("logServiceImport", "logPdfImport")
                .process("documentImport")
                .aggregate(constant(true), new GroupedBodyAggregationStrategy())
                .completionSize(100)
                .completionTimeout(2000)
                .bean("importEntityCache", "clear")
                .log(LoggingLevel.INFO, "de.muenchen.eh", "'${body.size}' pdf files imported.")
                .to(ClaimRouteBuilder.PROCESS_CLAIMS);

    }

}
