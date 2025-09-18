package de.muenchen.eh.kvue.file;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ImportDataEnricher implements Processor {

    @Produce(value = FileImportRouteBuilder.CLAIM_IMPORT_DATA_UNMARSHALL)
    private ProducerTemplate unmarshalImportDataProducer;

    private ImportDataWrapper dataWrapper;

    @Override
    public void process(Exchange exchange) throws Exception {

        dataWrapper = new ImportDataWrapper();

        dataWrapper.setClaimRawData(exchange.getMessage().getBody(String.class));
        Exchange unmarshalledData = unmarshallClaimImportData(exchange);
        dataWrapper.setImportClaimIdentifierData(unmarshalledData.getMessage().getBody(ImportClaimIdentifierData.class));
        dataWrapper.getImportClaimIdentifierData().setPrintDate(DateExtractor.extractDate(exchange.getMessage().getHeader(AWS2S3Constants.KEY, String.class)));
        exchange.getMessage().setBody(dataWrapper);

    }

    private Exchange unmarshallClaimImportData(Exchange exchange) {
        Exchange marshalContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(dataWrapper.getClaimRawData()).build();
        return unmarshalImportDataProducer.send(marshalContent);
    }
}
