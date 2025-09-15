package de.muenchen.eh.log.db;

import de.muenchen.eh.common.ExtractEhIdentifier;
import de.muenchen.eh.kvue.cases.EhCaseData;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class EhServiceImport {

    @Value("${xjustiz.interface.file.consume}")
    private String dataSource;

    protected final ImportRepository importRepository;
    protected final ImportLogRepository importLogRepository;

   public void logImportEh(final Exchange exchange) {

        try {
            ImportEntity importEntity = new ImportEntity();

            importEntity.setSourceFileName(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class));
            importEntity.setFileLineIndex((Integer) exchange.getAllProperties().get(Exchange.SPLIT_INDEX));
            importEntity.setStorageLocation(dataSource);
            importEntity.setContent(exchange.getIn().getHeader(Constants.EH_RAW_CONTENT, String.class));

            var caseImportEntity = exchange.getIn().getHeader(Constants.EH_CASE_DATA, EhCaseData.class);
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
            var fileName = exchange.getIn().getHeader(Exchange.FILE_NAME_CONSUMED, String.class);
            List<ImportEntity> importEntities = importRepository.findByOutputDirectory(ExtractEhIdentifier.getIdentifier(fileName));
            importEntities.forEach(entity -> {
            if (fileName.toUpperCase().endsWith(EXTENSION)) {
                    entity.setIsAntragImport(true);
            } else {
                    entity.setIsBescheidImport(true);
                }
                exchange.getIn().setHeader(Constants.IMPORT_ENTITY, importRepository.save(entity));
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
