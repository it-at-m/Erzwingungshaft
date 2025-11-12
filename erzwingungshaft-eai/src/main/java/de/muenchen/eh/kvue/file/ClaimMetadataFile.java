package de.muenchen.eh.kvue.file;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClaimMetadataFile implements Processor {

    @Produce(value = FileImportRouteBuilder.S3_UPLOAD)
    private ProducerTemplate s3Producer;

    @Override
    public void process(Exchange exchange) throws Exception {

        ImportDataWrapper dataWrapper = exchange.getMessage().getBody(ImportDataWrapper.class);

        var fileName = dataWrapper.getImportClaimIdentifierData().getPathName().concat("/").concat(dataWrapper.getImportClaimIdentifierData().getFileName());
        Exchange marshalContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(dataWrapper.getClaimRawData())
                .withHeader(AWS2S3Constants.KEY, fileName).build();
        s3Producer.send(marshalContent);

    }
}
