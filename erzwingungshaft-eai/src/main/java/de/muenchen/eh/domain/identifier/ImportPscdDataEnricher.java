package de.muenchen.eh.domain.identifier;

import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

@Component
public class ImportPscdDataEnricher implements Processor {

    @Produce(value = IdentifierRouteBuilder.PSCD_IDENTIFIER_CONTENT_UNMARSHALL)
    private ProducerTemplate unmarshallFixedLength;

    @Override
    public void process(Exchange exchange) throws Exception {

        EfileIdentifier efileIdentifier = new EfileIdentifier();

        // Data source information
        efileIdentifier.setFileLineIndex((Integer) exchange.getAllProperties().get(Exchange.SPLIT_INDEX));
        efileIdentifier.setSourceFileName(exchange.getMessage().getHeader(AWS2S3Constants.KEY, String.class));

        // Raw data
        efileIdentifier.setContent(exchange.getMessage().getBody(String.class));

        // Map unmarshalled PscdDataImport to PscdDataExport
        Exchange unmarshalledContent = unmarshallPscdImport(exchange);
        PscdDataExport pscdDataExport = PscdDataMapper.INSTANCE.toPscdExport(unmarshalledContent.getMessage().getBody(PscdDataImport.class));

        efileIdentifier.setGeschaeftspartnerId(pscdDataExport.getGeschaeftspartnerid());
        efileIdentifier.setKassenzeichen(pscdDataExport.getKassenzeichen());

        IdentifierContentWrapper identifierContentWrapper = new IdentifierContentWrapper();
        identifierContentWrapper.setEfileIdentifier(efileIdentifier);

        identifierContentWrapper.setPscdDataExport(pscdDataExport);
        exchange.getMessage().setBody(identifierContentWrapper);

    }

    private Exchange unmarshallPscdImport(Exchange exchange) {
        Exchange marshallContent = ExchangeBuilder.anExchange(exchange.getContext()).withBody(exchange.getMessage().getBody(String.class)).build();
        return unmarshallFixedLength.send(marshallContent);
    }

}
