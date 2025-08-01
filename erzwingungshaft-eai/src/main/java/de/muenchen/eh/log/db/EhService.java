package de.muenchen.eh.log.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.kvue.EhCase;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.convert.DataEntityMapper;
import de.muenchen.eh.log.db.entity.*;
import de.muenchen.eh.log.db.repository.*;
import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import jakarta.xml.bind.JAXBContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.Arrays;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class EhService {

    @Value("${xjustiz.interface.file.consume}")
    private String dataSource;

    private final EntryRepository entryRepository;
    private final ContentRepository contentRepository;
    private final DataRepository dataRepository;
    private final XmlRepository xmlRepository;
    private final LogRepository logRepository;

    private final Jackson2ObjectMapperBuilder mapperBuilder;

    public void logEntry(final Exchange exchange) {

        try {
            EntryEntity entryEntity = new EntryEntity();

            entryEntity.setSourceFileName(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class));
            entryEntity.setFileLineIndex((Integer) exchange.getAllProperties().get(Exchange.SPLIT_INDEX));
            entryEntity.setStorageLocation(dataSource);

            exchange.getIn().setHeader(Constants.ENTRY_ENTITY, entryRepository.save(entryEntity));

            writeInfoLogMessage(StatusProcessingType.DATA_READ, exchange);

        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void logUnmarshall(final Exchange exchange) {

        try {
            var dataEntity = DataEntityMapper.INSTANCE.toDataEntity(exchange.getIn().getBody(EhCase.class));
            dataEntity.setEntryId(EntityFactory.entryEntityFacade(exchange).getId());
            dataRepository.save(dataEntity);
            writeInfoLogMessage(StatusProcessingType.DATA_UNMARSHALLED, exchange);

            var entryEntity = EntityFactory.entryEntityFacade(exchange);
            entryEntity.setKassenzeichen(dataEntity.getEhkassz());
            entryEntity.setGeschaeftspartnerId(dataEntity.getEhgpid());
            entryRepository.save(entryEntity);
            writeInfoLogMessage(StatusProcessingType.EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    public void logContent(final Exchange exchange) {

        try {
            ContentContainer contentContainer = exchange.getIn().getBody(ContentContainer.class);
            ContentEntity contentEntity = (ContentEntity) EntityFactory.configureEntity(new ContentEntity(), exchange);
            ObjectMapper mapper = mapperBuilder.build();
            contentEntity.setJson(mapper.writeValueAsString(contentContainer));
            contentRepository.save(contentEntity);
            writeInfoLogMessage(StatusProcessingType.CONTENT_CREATED, exchange);

        } catch (JsonProcessingException e) {
            exchange.setException(e);
        }
    }

    public void logError(final Exchange exchange) {

        try {
            LogEntity logEntity = (LogEntity) EntityFactory.configureEntity(new LogEntity(), exchange);
            logEntity.setMessage_typ(MessageType.ERROR);
            logEntity.setMessage(exchange.getException() != null ? exchange.getException().getMessage() : ((Exception) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT)).getMessage());
            var stack = exchange.getException() != null ? exchange.getException().getStackTrace() : ((Exception) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT)).getStackTrace();
            logEntity.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
            logRepository.save(logEntity);
        } catch (Exception e) {
            exchange.setException(e);
        }

    }

    public void logXml(final Exchange exchange) {

        try {
            String xml = exchange.getIn().getBody(String.class);

            XmlEntity xmlEntity = (XmlEntity) EntityFactory.configureEntity(new XmlEntity(), exchange);
            xmlEntity.setContent(xml);
            xmlRepository.save(xmlEntity);
            writeInfoLogMessage(StatusProcessingType.XJUSTIZ_MESSAGE_CREATED, exchange);

            var entryEntity = EntityFactory.entryEntityFacade(exchange);
            NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 justiz0500010 = parseXML(xml);
            entryEntity.setEhUuid(UUID.fromString(justiz0500010.getNachrichtenkopf().getAbsender().getEigeneNachrichtenID()));
            entryRepository.save(entryEntity);
            writeInfoLogMessage(StatusProcessingType.EH_UUID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    private void writeInfoLogMessage(StatusProcessingType processingType, Exchange exchange) {
        try {
            LogEntity logEntity = (LogEntity) EntityFactory.configureEntity(new LogEntity(), exchange);
            logEntity.setMessage_typ(MessageType.INFO);
            logEntity.setMessage(processingType.name());
            logEntity.setComment(processingType.getDescriptor());
            logRepository.save(logEntity);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    protected NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 parseXML(String xml) throws Exception {
        JAXBContext context = JAXBContext.newInstance(NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010.class);
        return (NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010) context.createUnmarshaller().unmarshal(new StringReader(xml));
    }

}
