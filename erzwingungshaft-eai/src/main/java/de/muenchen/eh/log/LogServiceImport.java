package de.muenchen.eh.log;

import de.muenchen.eh.common.FileNameUtils;
import de.muenchen.eh.db.ImportEntityCache;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.entity.ClaimImportLog;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimImportLogRepository;
import de.muenchen.eh.db.repository.ClaimImportRepository;
import de.muenchen.eh.file.ImportClaimIdentifierData;
import de.muenchen.eh.file.ImportContentWrapper;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            ImportContentWrapper dataWrapper = exchange.getMessage().getBody(ImportContentWrapper.class);

            ClaimImport claimImport = new ClaimImport();

            claimImport.setSourceFileName(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
            claimImport.setFileLineIndex((Integer) exchange.getAllProperties().get(Exchange.SPLIT_INDEX));
            claimImport.setStorageLocation(dataSource);
            claimImport.setContent(dataWrapper.getClaimRawData());

            ImportClaimIdentifierData caseImportEntity = dataWrapper.getImportClaimIdentifierData();
            claimImport.setKassenzeichen(caseImportEntity.getEhkassz());
            claimImport.setGeschaeftspartnerId(caseImportEntity.getEhgpid());
            claimImport.setErstellDatum(caseImportEntity.getPrintDate());
            claimImport.setOutputDirectory(caseImportEntity.getPathName());
            claimImport.setOutputFile(caseImportEntity.getFileName());
            claimImport.setIsDataImport(true);

            exchange.setProperty(Constants.CLAIM_IMPORT, claimImportRepository.save(claimImport));

            writeInfoImportLogMessage(StatusProcessingType.IMPORT_DATA_FILE_CREATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void logPdfImport(final Exchange exchange) {

        try {
            var fileName = FileNameUtils.getFileName(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
            var ehkasszEhgpidPrintDate = FileNameUtils.getIdentifier(fileName);
            List<ClaimImport> claimImports = claimImportCache.getImportEntities(ehkasszEhgpidPrintDate);

            AtomicReference<StatusProcessingType> type = new AtomicReference<>();
            claimImports.forEach(claimImport -> {
                if (FileNameUtils.isEHFile(fileName)) {
                    claimImport.setIsAntragImport(true);
                    type.set(StatusProcessingType.IMPORT_ANTRAG_IMPORT_DIRECTORY);
                } else if (FileNameUtils.isURBFile(fileName)) {
                    claimImport.setIsBescheidImport(true);
                    type.set(StatusProcessingType.IMPORT_BESCHEID_IMPORT_DIRECTORY);
                } else if (FileNameUtils.isVWFile(fileName)) {
                    claimImport.setIsVerwerfungBescheidImport(true);
                    type.set(StatusProcessingType.IMPORT_VERWERFUNG_BESCHEID_IMPORT_DIRECTORY);
                } else if (FileNameUtils.isURKFile(fileName)) {
                    claimImport.setIsKostenBescheidImport(true);
                    type.set(StatusProcessingType.IMPORT_KOSTEN_BESCHEID_IMPORT_DIRECTORY);
                } else {
                    exchange.setException(new Exception("Unkown File: " + fileName));
                }

                var updateClaimImport = claimImportRepository.save(claimImport);
                claimImportCache.put(ehkasszEhgpidPrintDate, updateClaimImport);
                exchange.setProperty(Constants.CLAIM_IMPORT, updateClaimImport);
                writeInfoImportLogMessage(type.get(), exchange);

            });
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void writeInfoImportLogMessage(StatusProcessingType processingType, Exchange exchange) {
        try {

            ClaimImportLog claimImportLog = new ClaimImportLog();
            ClaimImport claimImport = exchange.getProperty(Constants.CLAIM_IMPORT, ClaimImport.class);
            claimImportLog.setClaimImportId(claimImport.getId());
            claimImportLog.setMessageType(MessageType.INFO);
            claimImportLog.setMessage(processingType.name());
            claimImportLog.setComment(processingType.getDescriptor());
            claimImportLogRepository.save(claimImportLog);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

}
