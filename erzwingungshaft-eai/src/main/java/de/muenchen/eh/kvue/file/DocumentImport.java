package de.muenchen.eh.kvue.file;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.DocumentType;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceImport;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
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

        claimDocument.setDocumentType(claimDocument.getFileName().toUpperCase().endsWith(Constants.ANTRAG_EXTENSION) ? DocumentType.ANTRAG.getDescriptor()
                : DocumentType.BESCHEID.getDescriptor());

        ClaimImport claimImport = exchange.getProperty(Constants.CLAIM_IMPORT, ClaimImport.class);
        claimDocument.setClaimImportId(claimImport.getId());

        claimDocument.setDocument(exchange.getIn().getBody(byte[].class));
        claimDocumentRepository.save(claimDocument);

        logServiceImport.writeInfoImportLogMessage(
                claimDocument.getFileName().toUpperCase().endsWith(Constants.ANTRAG_EXTENSION) ? StatusProcessingType.IMPORT_ANTRAG_IMPORT_DB
                        : StatusProcessingType.IMPORT_BESCHEID_IMPORT_DB,
                exchange);

    }
}
