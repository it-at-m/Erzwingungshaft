package de.muenchen.eh.log.db;

import de.muenchen.eh.common.ExtractEhIdentifier;
import de.muenchen.eh.kvue.file.ImportDataWrapper;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.db.entity.ImportEntity;
import de.muenchen.eh.log.db.entity.ImportLogEntity;
import de.muenchen.eh.log.db.entity.MessageType;
import de.muenchen.eh.log.db.repository.ImportLogRepository;
import de.muenchen.eh.log.db.repository.ImportRepository;
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
public class EhServiceImport {

    @Value("${xjustiz.interface.file.consume}")
    private String dataSource;

    private final ImportRepository importRepository;
    private final ImportLogRepository importLogRepository;

    private final ImportEntityCache importEntityCache;


   public void logImportEh(final Exchange exchange) {

        try {
            ImportDataWrapper dataWrapper = exchange.getMessage().getBody(ImportDataWrapper.class);

            ImportEntity importEntity = new ImportEntity();

            importEntity.setSourceFileName(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
            importEntity.setFileLineIndex((Integer) exchange.getAllProperties().get(Exchange.SPLIT_INDEX));
            importEntity.setStorageLocation(dataSource);
            importEntity.setContent(dataWrapper.getClaimRawData());

            var caseImportEntity = dataWrapper.getImportClaimIdentifierData();
            importEntity.setKassenzeichen(caseImportEntity.getEhkassz());
            importEntity.setGeschaeftspartnerId(caseImportEntity.getEhgpid());
            importEntity.setOutputDirectory(caseImportEntity.getPathName());
            importEntity.setOutputFile(caseImportEntity.getFileName());
            importEntity.setIsDataImport(true);

            exchange.getIn().setHeader(Constants.IMPORT_ENTITY, importRepository.save(importEntity));

            writeInfoImportLogMessage(StatusProcessingType.DATA_FILE_CREATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void logImportPdf(final Exchange exchange) {
        final String EXTENSION = "_EH.PDF";
        try {
            var fileName = ExtractEhIdentifier.getFileName(exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class));
            var ehkasszEhgpidPrintDate = ExtractEhIdentifier.getIdentifier(fileName);
            List<ImportEntity> importEntities = importEntityCache.getImportEntities(ehkasszEhgpidPrintDate);
            importEntities.forEach(entity -> {
            if (fileName.toUpperCase().endsWith(EXTENSION)) {
                    entity.setIsAntragImport(true);
            } else {
                    entity.setIsBescheidImport(true);
            }
            var updateImportEntity =  importRepository.save(entity);
            importEntityCache.put(ehkasszEhgpidPrintDate, updateImportEntity);
            exchange.getIn().setHeader(Constants.IMPORT_ENTITY, updateImportEntity);
            writeInfoImportLogMessage(fileName.toUpperCase().endsWith(EXTENSION) ? StatusProcessingType.ANTRAG_IMPORT : StatusProcessingType.BESCHEID_IMPORT , exchange);
            }
            );
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    private void writeInfoImportLogMessage(StatusProcessingType processingType, Exchange exchange) {
        try {

            ImportLogEntity importLogEntity = new ImportLogEntity();
            ImportEntity importEntity = exchange.getIn().getHeader(Constants.IMPORT_ENTITY, ImportEntity.class);
            importLogEntity.setImportId(importEntity.getId());
            importLogEntity.setMessageTyp(MessageType.INFO);
            importLogEntity.setMessage(processingType.name());
            importLogEntity.setComment(processingType.getDescriptor());
            importLogRepository.save(importLogEntity);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }


}
