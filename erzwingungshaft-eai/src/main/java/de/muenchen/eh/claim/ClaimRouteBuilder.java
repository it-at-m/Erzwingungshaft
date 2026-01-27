package de.muenchen.eh.claim;

import de.muenchen.eh.BaseRouteBuilder;
import de.muenchen.eh.db.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimRouteBuilder extends BaseRouteBuilder {

    public static final String PROCESS_CLAIMS = "direct:processClaims";
    public static final String UNMARSHALL_EH_CLAIM_DATA = "direct:unmarshalEhClaimData";
    public static final String PROCESS_XJUSTIZ_DOCUMENT = "direct:processXjustizDocument";

    private final ClaimService claimService;

    @Override
    public void configure() {

        super.configure();

        // spotless:off
        from(PROCESS_CLAIMS)
                .routeId("claim-eh-process")
                .log(LoggingLevel.INFO, "Starting process eh-process ...")
                .setBody(method(claimService, "claimsForProcessing"))
                .split().body().aggregationStrategy(new GroupedBodyAggregationStrategy())
                    .process("claimDataUnmarshaller")
                    .process("claimContentDataEnricher")
                    .process("claimXJustizXmlEnricher")
                    .process("efilesOperationExecutor")
                    .process("{{xjustiz.interface.xta}}").id("bebpoService")
                    .log(LoggingLevel.DEBUG, "claim-eh-process completed gpid '${body.claimImport.geschaeftspartnerId}'.").id("claim-eh-process-gpid")
                .end()
                .bean("findCollection", "clearCollectionCache");

        from(UNMARSHALL_EH_CLAIM_DATA).routeId("unmarshal-eh-claimdata")
                .unmarshal().bindy(BindyType.Fixed, ImportClaimData.class)
                .log(LoggingLevel.DEBUG, "unmarshal-eh-claimdata : ${body}");

        from(PROCESS_XJUSTIZ_DOCUMENT).routeId("process-xjustiz-document")
                .to("{{xjustiz.interface.document.processor}}");

        // spotless:on

    }

}
