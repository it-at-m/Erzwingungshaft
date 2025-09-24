package de.muenchen.eh.log.db;

import de.muenchen.eh.common.ExtractEhIdentifier;
import de.muenchen.eh.kvue.file.ImportDataWrapper;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.eh.log.db.entity.ClaimImportLog;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.log.db.repository.ClaimImportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class LogServiceImport {

    @Value("${xjustiz.interface.file.consume}")
    private String dataSource;

    private final ClaimImportRepository claimImportRepository;
    private final ClaimImportLogRepository claimImportLogRepository;

    private final ImportEntityCache claimImportCache;


    public void logClaimImport(final Exchange exchange) {

        try {
            ImportDataWrapper dataWrapper = exchange.getMessage().getBody(ImportDataWrapper.class);

            ClaimImport claimImport = new ClaimImport();

            claimImport.setSourceFileName(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
            claimImport.setFileLineIndex((Integer) exchange.getAllProperties().get(Exchange.SPLIT_INDEX));
            claimImport.setStorageLocation(dataSource);
            claimImport.setContent(dataWrapper.getClaimRawData());

            var caseImportEntity = dataWrapper.getImportClaimIdentifierData();
            claimImport.setKassenzeichen(caseImportEntity.getEhkassz());
            claimImport.setGeschaeftspartnerId(caseImportEntity.getEhgpid());
            claimImport.setOutputDirectory(caseImportEntity.getPathName());
            claimImport.setOutputFile(caseImportEntity.getFileName());
            claimImport.setIsDataImport(true);

            exchange.getIn().setHeader(Constants.CLAIM_IMPORT, claimImportRepository.save(claimImport));

            writeInfoImportLogMessage(StatusProcessingType.DATA_FILE_CREATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void logPdfImport(final Exchange exchange) {

        try {
            var fileName = ExtractEhIdentifier.getFileName(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
            var ehkasszEhgpidPrintDate = ExtractEhIdentifier.getIdentifier(fileName);
            List<ClaimImport> claimImports = claimImportCache.getImportEntities(ehkasszEhgpidPrintDate);
            claimImports.forEach(claimImport -> {
                        if (fileName.toUpperCase().endsWith(Constants.ANTRAG_EXTENSION)) {
                            claimImport.setIsAntragImport(true);
                        } else {
                            claimImport.setIsBescheidImport(true);
                        }
                        var updateClaimImport = claimImportRepository.save(claimImport);
                        claimImportCache.put(ehkasszEhgpidPrintDate, updateClaimImport);
                        exchange.getIn().setHeader(Constants.CLAIM_IMPORT, updateClaimImport);
                        writeInfoImportLogMessage(fileName.toUpperCase().endsWith(Constants.ANTRAG_EXTENSION) ? StatusProcessingType.ANTRAG_IMPORT_DIRECTORY : StatusProcessingType.BESCHEID_IMPORT_DIRECTORY, exchange);
                    }
            );
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void writeInfoImportLogMessage(StatusProcessingType processingType, Exchange exchange) {
        try {

            ClaimImportLog claimImportLog = new ClaimImportLog();
            ClaimImport claimImport = exchange.getIn().getHeader(Constants.CLAIM_IMPORT, ClaimImport.class);
            claimImportLog.setClaimImportId(claimImport.getId());
            claimImportLog.setMessageTyp(MessageType.INFO);
            claimImportLog.setMessage(processingType.name());
            claimImportLog.setComment(processingType.getDescriptor());
            claimImportLogRepository.save(claimImportLog);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }


}
