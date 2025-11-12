package de.muenchen.eh.kvue.file;

import de.muenchen.eh.common.ExtractEhIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;

@Slf4j
public class S3ObjectName implements Processor {
    @Override
    public void process(Exchange exchange) {

        String originalKey = exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class);
        if (originalKey == null || originalKey.isEmpty()) {
            throw new IllegalArgumentException("AWS S3 key header is missing or empty");
        }

        try {
            String prefix = ExtractEhIdentifier.getIdentifier(originalKey);
            String updatedKey = prefix + "/" + originalKey;
            exchange.getIn().setHeader(AWS2S3Constants.KEY, updatedKey);
            log.debug("Aws2S3.Key: {}", updatedKey);
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            exchange.setException(new Exception("Invalid S3 key format: " + originalKey, e));
        }

    }
}
