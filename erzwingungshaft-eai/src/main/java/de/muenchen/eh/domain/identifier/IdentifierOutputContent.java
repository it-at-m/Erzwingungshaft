package de.muenchen.eh.domain.identifier;

import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

@Component
public class IdentifierOutputContent implements Processor {

    @Produce(value = IdentifierRouteBuilder.PSCD_OUTPUT_MARSHALL)
    private ProducerTemplate outputMarshall;

    @Produce(value = IdentifierRouteBuilder.S3_OUTPUT_UPLOAD)
    private ProducerTemplate s3Upload;

    @Override
    public void process(Exchange exchange) throws Exception {

        List<IdentifierContentWrapper> imported = (List<IdentifierContentWrapper>) exchange.getMessage().getBody();
        List<PscdDataExport> identifiers = imported.stream().map(IdentifierContentWrapper::getPscdDataExport)
                .filter(identifier -> (identifier.getGeschaeftszeicheneakte() != null && !identifier.getGeschaeftszeicheneakte().isEmpty())).toList();

        Exchange responseMarshall = outputMarshall.send(ExchangeBuilder.anExchange(exchange.getContext()).withBody(identifiers).build());
        s3Upload.send(ExchangeBuilder.anExchange(exchange.getContext()).withBody(responseMarshall.getMessage().getBody())
                .withHeader(AWS2S3Constants.KEY, imported.getFirst().getEfileIdentifier().getOutputFileName()).build());

    }
}
