package de.muenchen.eh.kvue.cases;

import de.muenchen.eh.kvue.BaseRouteBuilder;
import de.muenchen.eh.kvue.claim.EhClaimRouteBuilder;
import de.muenchen.eh.log.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EhImportRouteBuilder extends BaseRouteBuilder {

    @Override
    public void configure() {

        super.configure();

        from("{{xjustiz.interface.file.consume}}")
                .routeId("generate-import-files")
                .split(body().tokenize(lineBreak))
                .setHeader(Constants.EH_RAW_CONTENT, simple("${body}"))
                .to("log:de.muenchen.eh?level=DEBUG")
                .unmarshal().bindy(BindyType.Fixed, EhCaseData.class)
                .setHeader(Constants.EH_CASE_DATA, simple("${body}"))
                .setBody(simple(String.format("${header.%s}", Constants.EH_RAW_CONTENT)))
                .toD(String.format("{{xjustiz.interface.file.file-output}}", Constants.EH_CASE_DATA))
                .bean("ehServiceImport", "logImportEh")
                .end()
                .process(exchange -> {
                    exchange.getContext().getRouteController().startRoute("import-pdfs");
                });


//        Das muss zeitlich nach dem daten import.
//        Aktuell wird nur ein Dokument (Bescheid) korrekt gelogt. Der Antrag ist kopiert aber nicht gelogt.

        from("{{xjustiz.interface.pdf.consume}}")
                .routeId("import-pdfs")
                .autoStartup(false)
                .split().body()

//                Gesucht ist eine Lösung für den S3, nicht für die File Komponente :-)
//
//                batchCosnumer funktioniert noch nicht richtig, gibt es noch eine andere option den start der nachsten route zu verzögern ?

                .setHeader(Exchange.FILE_NAME_CONSUMED, simple("${body.fileName}"))
                .process(new EhIdentifier())
                .toD(String.format("{{xjustiz.interface.pdf.file-output}}", Constants.EH_IDENTIFIER))
                .bean("ehServiceImport", "logImportPdf")
                .choice()
                    .when(header("CamelBatchComplete").isEqualTo(true)) //
                        .setBody().constant("PDF imports completed. Start case processing. ")
                        .to(EhClaimRouteBuilder.PROCESS_CLAIMS)
                .end();

    }



}
