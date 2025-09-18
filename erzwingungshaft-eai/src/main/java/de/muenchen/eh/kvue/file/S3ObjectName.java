package de.muenchen.eh.kvue.file;

import de.muenchen.eh.common.ExtractEhIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;

@Slf4j
public class S3ObjectName implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        var prefix = ExtractEhIdentifier.getIdentifier(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
        exchange.getIn().setHeader(AWS2S3Constants.KEY, prefix.concat("/").concat(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class)));
        log.debug("Aws2S3.Key : {} " , exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
    }
}
