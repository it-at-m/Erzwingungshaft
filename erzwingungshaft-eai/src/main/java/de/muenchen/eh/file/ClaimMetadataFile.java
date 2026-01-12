package de.muenchen.eh.file;

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

        ImportContentWrapper importContentWrapper = exchange.getMessage().getBody(ImportContentWrapper.class);

        var fileName = importContentWrapper.getImportClaimIdentifierData().getPathName().concat("/")
                .concat(importContentWrapper.getImportClaimIdentifierData().getFileName());
        Exchange marshalContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(importContentWrapper.getClaimRawData())
                .withHeader(AWS2S3Constants.KEY, fileName).build();
        s3Producer.send(marshalContent);

    }
}
