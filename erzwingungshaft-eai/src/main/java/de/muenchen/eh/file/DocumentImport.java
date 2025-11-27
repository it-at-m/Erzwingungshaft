package de.muenchen.eh.file;

import de.muenchen.eh.common.FileNameUtils;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.LogServiceImport;
import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentImport implements Processor {

    private final ClaimDocumentRepository claimDocumentRepository;
    private final LogServiceImport logServiceImport;

    @Override
    public void process(Exchange exchange) throws Exception {

        ClaimDocument claimDocument = new ClaimDocument();

        claimDocument.setFileTyp(exchange.getMessage().getHeader(AWS2S3Constants.CONTENT_TYPE, String.class));
        claimDocument.setUploadedOn(LocalDateTime.now());
        claimDocument.setDocumentReference(UUID.randomUUID());
        claimDocument.setFileName(exchange.getMessage().getHeader(AWS2S3Constants.KEY, String.class));
        claimDocument.setFile_size(exchange.getMessage().getHeader(AWS2S3Constants.CONTENT_LENGTH, Long.class));

        claimDocument.setUpdatedOn(exchange.getMessage().getHeader(AWS2S3Constants.LAST_MODIFIED, LocalDateTime.class));
        claimDocument.setAwsS3ETag(exchange.getMessage().getHeader(AWS2S3Constants.E_TAG, String.class));

        claimDocument.setDocumentType(FileNameUtils.getDocumentType(claimDocument.getFileName()).getDescriptor());

        ClaimImport claimImport = exchange.getProperty(Constants.CLAIM_IMPORT, ClaimImport.class);
        claimDocument.setClaimImportId(claimImport.getId());

        claimDocument.setDocument(exchange.getIn().getBody(byte[].class));
        claimDocumentRepository.save(claimDocument);

        logServiceImport.writeInfoImportLogMessage(FileNameUtils.getProcessingType(claimDocument.getFileName()) , exchange);

    }
}
