package de.muenchen.eh.log.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.common.BindyIllegalArgumentMessageEnricher;
import de.muenchen.eh.common.XmlUnmarshaller;
import de.muenchen.eh.kvue.claim.ClaimData;
import de.muenchen.eh.kvue.claim.ClaimDataWrapper;
import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.eh.log.convert.DataEntityMapper;
import de.muenchen.eh.log.db.entity.*;
import de.muenchen.eh.log.db.repository.*;
import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;



@Service
@Log4j2
@RequiredArgsConstructor
public class EhServiceClaim {

    @Value("${xjustiz.interface.file.consume}")
    private String dataSource;

    private final ClaimRepository claimRepository;
    private final ClaimContentRepository claimContentRepository;
    private final ClaimDataRepository claimDataRepository;
    private final ClaimXmlRepository claimXmlRepository;
    private final ClaimLogRepository claimLogRepository;

    private final Jackson2ObjectMapperBuilder mapperBuilder;


    public void logEntry(final Exchange exchange) {

        try {
            ClaimEntity entryEntity = new ClaimEntity();

            ImportEntity importedClaim = exchange.getMessage().getBody(ClaimDataWrapper.class).getImportEntity();
            entryEntity.setImportId(importedClaim.getId());
            entryEntity.setSourceFileName(importedClaim.getSourceFileName());
            entryEntity.setFileLineIndex(importedClaim.getFileLineIndex());
            entryEntity.setStorageLocation(dataSource);

            exchange.getMessage().setHeader(Constants.ENTRY_ENTITY, claimRepository.save(entryEntity));

            writeInfoClaimLogMessage(StatusProcessingType.DATA_READ, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logUnmarshall(final Exchange exchange) {

        try {
            var dataEntity = DataEntityMapper.INSTANCE.toClaimDataEntity(exchange.getMessage().getBody(ClaimDataWrapper.class).getEhClaimData());
            dataEntity.setClaimId(ClaimFactory.entryEntityFacade(exchange).getId());
            claimDataRepository.save(dataEntity);
            writeInfoClaimLogMessage(StatusProcessingType.DATA_UNMARSHALLED, exchange);

            var entryEntity = ClaimFactory.entryEntityFacade(exchange);
            entryEntity.setKassenzeichen(dataEntity.getEhkassz());
            entryEntity.setGeschaeftspartnerId(dataEntity.getEhgpid());
            claimRepository.save(entryEntity);
            writeInfoClaimLogMessage(StatusProcessingType.EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logIllegalArgumentException(final Exchange exchange) {

        try {
            ClaimLogEntity claimLogEntity = (ClaimLogEntity) ClaimFactory.configureEntity(new ClaimLogEntity(), exchange);
            claimLogEntity.setMessageTyp(MessageType.ERROR);
            var message = EhServiceError.getMessage(exchange);
            claimLogEntity.setMessage(BindyIllegalArgumentMessageEnricher.enrich(message, ClaimData.class));
            var stack = EhServiceError.getStack(exchange);
            claimLogEntity.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
            claimLogRepository.save(claimLogEntity);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }

    }

    public void logContent(final Exchange exchange) {

        try {
            ContentContainer contentContainer = exchange.getMessage().getBody(ClaimDataWrapper.class).getContentContainer();
            ClaimContentEntity claimContentEntity = (ClaimContentEntity) ClaimFactory.configureEntity(new ClaimContentEntity(), exchange);
            ObjectMapper mapper = mapperBuilder.build();
            claimContentEntity.setJson(mapper.writeValueAsString(contentContainer));
            claimContentRepository.save(claimContentEntity);
            writeInfoClaimLogMessage(StatusProcessingType.CONTENT_CREATED, exchange);

        } catch (JsonProcessingException e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logXml(final Exchange exchange) {

        try {
            String xml = exchange.getMessage().getBody(String.class);

            ClaimXmlEntity claimXmlEntity = (ClaimXmlEntity) ClaimFactory.configureEntity(new ClaimXmlEntity(), exchange);
            claimXmlEntity.setContent(xml);
            claimXmlRepository.save(claimXmlEntity);
            writeInfoClaimLogMessage(StatusProcessingType.XJUSTIZ_MESSAGE_CREATED, exchange);

            var entryEntity = ClaimFactory.entryEntityFacade(exchange);
            NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 justiz0500010 = XmlUnmarshaller.unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(xml);
            entryEntity.setEhUuid(UUID.fromString(justiz0500010.getNachrichtenkopf().getAbsender().getEigeneNachrichtenID()));
            claimRepository.save(entryEntity);
            writeInfoClaimLogMessage(StatusProcessingType.EH_UUID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    private void writeInfoClaimLogMessage(StatusProcessingType processingType, Exchange exchange) {
        try {
            ClaimLogEntity claimLogEntity = (ClaimLogEntity) ClaimFactory.configureEntity(new ClaimLogEntity(), exchange);
            claimLogEntity.setMessageTyp(MessageType.INFO);
            claimLogEntity.setMessage(processingType.name());
            claimLogEntity.setComment(processingType.getDescriptor());
            claimLogRepository.save(claimLogEntity);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }



}
