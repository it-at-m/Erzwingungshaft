package de.muenchen.eh.log.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.common.BindyIllegalArgumentMessageEnricher;
import de.muenchen.eh.common.XmlUnmarshaller;
import de.muenchen.eh.kvue.claim.ImportClaimData;
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


    public void logClaim(final Exchange exchange) {

        try {
            ClaimEntity claim = new ClaimEntity();

            ClaimImport importedClaim = exchange.getMessage().getBody(ClaimDataWrapper.class).getClaimImport();
            claim.setClaimImportId(importedClaim.getId());
            claim.setSourceFileName(importedClaim.getSourceFileName());
            claim.setFileLineIndex(importedClaim.getFileLineIndex());
            claim.setStorageLocation(dataSource);

            exchange.getMessage().setHeader(Constants.CLAIM, claimRepository.save(claim));

            writeInfoClaimLogMessage(StatusProcessingType.DATA_READ, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logUnmarshall(final Exchange exchange) {

        try {
            ClaimData claimData = DataEntityMapper.INSTANCE.toClaimDataEntity(exchange.getMessage().getBody(ClaimDataWrapper.class).getEhImportClaimData());

            claimData.setClaimId(ClaimFactory.claimFacade(exchange).getId());
            claimDataRepository.save(claimData);
            writeInfoClaimLogMessage(StatusProcessingType.DATA_UNMARSHALLED, exchange);

            var claim = ClaimFactory.claimFacade(exchange);
            claim.setKassenzeichen(claimData.getEhkassz());
            claim.setGeschaeftspartnerId(claimData.getEhgpid());
            claimRepository.save(claim);
            writeInfoClaimLogMessage(StatusProcessingType.EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logIllegalArgumentException(final Exchange exchange) {

        try {
            ClaimLog claimLog = (ClaimLog) ClaimFactory.configureEntity(new ClaimLog(), exchange);
            claimLog.setMessageTyp(MessageType.ERROR);
            var message = EhServiceError.getMessage(exchange);
            claimLog.setMessage(BindyIllegalArgumentMessageEnricher.enrich(message, ImportClaimData.class));
            var stack = EhServiceError.getStack(exchange);
            claimLog.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
            claimLogRepository.save(claimLog);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }

    }

    public void logContent(final Exchange exchange) {

        try {
            ContentContainer contentContainer = exchange.getMessage().getBody(ClaimDataWrapper.class).getContentContainer();
            ClaimContent claimContent = (ClaimContent) ClaimFactory.configureEntity(new ClaimContent(), exchange);
            ObjectMapper mapper = mapperBuilder.build();
            claimContent.setJson(mapper.writeValueAsString(contentContainer));
            claimContentRepository.save(claimContent);
            writeInfoClaimLogMessage(StatusProcessingType.CONTENT_CREATED, exchange);

        } catch (JsonProcessingException e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logXml(final Exchange exchange) {

        try {
            String xml = exchange.getMessage().getBody(ClaimDataWrapper.class).getXjustizXml()  ;
            ClaimXml claimXml = (ClaimXml) ClaimFactory.configureEntity(new ClaimXml(), exchange);
            claimXml.setContent(xml);
            claimXmlRepository.save(claimXml);
            writeInfoClaimLogMessage(StatusProcessingType.XJUSTIZ_MESSAGE_CREATED, exchange);

            var entryEntity = ClaimFactory.claimFacade(exchange);
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
            ClaimLog claimLog = (ClaimLog) ClaimFactory.configureEntity(new ClaimLog(), exchange);
            claimLog.setMessageTyp(MessageType.INFO);
            claimLog.setMessage(processingType.name());
            claimLog.setComment(processingType.getDescriptor());
            claimLogRepository.save(claimLog);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }



}
