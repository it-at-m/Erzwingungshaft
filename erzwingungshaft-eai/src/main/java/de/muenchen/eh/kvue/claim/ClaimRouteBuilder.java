package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.kvue.BaseRouteBuilder;
import de.muenchen.eh.log.db.repository.ClaimImportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimRouteBuilder extends BaseRouteBuilder {

    public static final String PROCESS_CLAIMS = "direct:processClaims";
    public static final String UNMARSHALL_EH_CLAIM_DATA = "direct:unmarshalEhClaimData";
    public static final String PROCESS_XJUSTIZ_DOCUMENT = "direct:processXjustizDocument";

    @Autowired
    private ClaimImportRepository claimImportRepository;

    @Override
    public void configure() {

        super.configure();

        from(PROCESS_CLAIMS)
                .routeId("application-eh-process")
//                .autoStartup(false)
//                 .split(body().tokenize(lineBreak))

                .setBody(method(claimImportRepository, "findByIsDataImportTrueAndIsAntragImportTrueAndIsBescheidImportTrue"))
                
                .split().body()
                .to("log:de.muenchen.eh?level=DEBUG")

        //        .pollEnrich().simple("file:testdata/out/?fileName=${body.getOutputDirectory()}/${body.getOutputFile()}")
                .process("claimDataUnmarshaller")

//                .bean("ehServiceClaim", "logEntry")
//                .setBody(simple("${body.getContent()}"))
//                .unmarshal().bindy(BindyType.Fixed, EhClaimData.class)

                .to("log:de.muenchen.eh?level=DEBUG")

//                .bean("ehServiceClaim", "logUnmarshall")

//                .pollEnrich("")
//                .transform().simple("${body.ehClaimData.supplyXJustizRequestContent()}")
                .process("claimContentDataEnricher")
 //               .bean("ehServiceClaim", "logContent")
 //               .to("{{xjustiz.interface.document.processor}}")
                .process("claimXJustizXmlEnricher")
                .to("log:de.muenchen.eh?level=DEBUG")
 //               .bean("ehServiceClaim", "logXml")
                .to("{{xjustiz.interface.eakte}}");

         from(UNMARSHALL_EH_CLAIM_DATA).routeId("unmarshal-eh-claimdata")
              .unmarshal().bindy(BindyType.Fixed, ImportClaimData.class);

         from(PROCESS_XJUSTIZ_DOCUMENT).routeId("process-xjustiz-document")
              .to("{{xjustiz.interface.document.processor}}");

    }

}
