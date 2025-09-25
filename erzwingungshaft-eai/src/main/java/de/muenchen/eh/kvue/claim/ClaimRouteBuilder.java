package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.kvue.BaseRouteBuilder;
import de.muenchen.eh.log.db.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.dataformat.BindyType;
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

        from(PROCESS_CLAIMS)
                .routeId("claim-eh-process")
                .setBody(method(claimService, "claimsForProcessing"))
                .split().body()
                .process("claimDataUnmarshaller")
                .process("claimContentDataEnricher")
                .process("claimXJustizXmlEnricher")
                .to("{{xjustiz.interface.eakte}}");

         from(UNMARSHALL_EH_CLAIM_DATA).routeId("unmarshal-eh-claimdata")
              .unmarshal().bindy(BindyType.Fixed, ImportClaimData.class);

         from(PROCESS_XJUSTIZ_DOCUMENT).routeId("process-xjustiz-document")
              .to("{{xjustiz.interface.document.processor}}");

    }

}
