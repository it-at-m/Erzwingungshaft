package de.muenchen.eh.kvue.file;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.PdfImportType;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.LogServiceImport;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

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

        claimDocument.setDocumentType(claimDocument.getFileName().toUpperCase().endsWith(Constants.ANTRAG_EXTENSION) ? PdfImportType.ANTRAG.getDescriptor() : PdfImportType.BESCHEID.getDescriptor());

        ClaimImport claimImport = exchange.getIn().getHeader(Constants.CLAIM_IMPORT, ClaimImport.class);
        claimDocument.setClaimImportId(claimImport.getId());

        claimDocument.setDocument(exchange.getIn().getBody(byte[].class));
        claimDocumentRepository.save(claimDocument);

        logServiceImport.writeInfoImportLogMessage(claimDocument.getFileName().toUpperCase().endsWith(Constants.ANTRAG_EXTENSION) ? StatusProcessingType.ANTRAG_IMPORT_DB : StatusProcessingType.BESCHEID_IMPORT_DB, exchange);

    }
}
