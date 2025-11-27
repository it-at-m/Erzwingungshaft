package de.muenchen.eh.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eh.common.BindyIllegalArgumentMessageEnricher;
import de.muenchen.eh.common.XmlUnmarshaller;
import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.ImportClaimData;
import de.muenchen.eh.log.convert.DataEntityMapper;
import de.muenchen.eh.db.entity.Claim;
import de.muenchen.eh.db.entity.ClaimContent;
import de.muenchen.eh.db.entity.ClaimData;
import de.muenchen.eh.db.entity.ClaimFactory;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.entity.ClaimLog;
import de.muenchen.eh.db.entity.ClaimXml;
import de.muenchen.eh.db.entity.MessageType;
import de.muenchen.eh.db.repository.ClaimContentRepository;
import de.muenchen.eh.db.repository.ClaimDataRepository;
import de.muenchen.eh.db.repository.ClaimLogRepository;
import de.muenchen.eh.db.repository.ClaimRepository;
import de.muenchen.eh.db.repository.ClaimXmlRepository;
import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import java.util.Arrays;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class LogServiceClaim {

    @Value("${xjustiz.interface.file.consume}")
    private String dataSource;

    @Getter
    @Value("${xjustiz.version}")
    private String xjustizVersion;

    private final ClaimRepository claimRepository;
    private final ClaimContentRepository claimContentRepository;
    private final ClaimDataRepository claimDataRepository;
    private final ClaimXmlRepository claimXmlRepository;
    private final ClaimLogRepository claimLogRepository;

    private final Jackson2ObjectMapperBuilder mapperBuilder;

    public void logClaim(final Exchange exchange) {

        try {

            ClaimContentWrapper processingDataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

            Claim claim = new Claim();

            ClaimImport importedClaim = processingDataWrapper.getClaimImport();
            claim.setClaimImport(importedClaim);
            claim.setSourceFileName(importedClaim.getSourceFileName());
            claim.setFileLineIndex(importedClaim.getFileLineIndex());
            claim.setStorageLocation(dataSource);

            processingDataWrapper.setClaim(claimRepository.save(claim));

            exchange.setProperty(Constants.CLAIM, processingDataWrapper.getClaim());

            writeInfoClaimLogMessage(StatusProcessingType.CLAIM_RAW_DATA_READ, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logUnmarshall(final Exchange exchange) {

        try {
            ClaimData claimData = DataEntityMapper.INSTANCE
                    .toClaimDataEntity(exchange.getMessage().getBody(ClaimContentWrapper.class).getEhImportClaimData());

            claimData.setClaimId(ClaimFactory.claimFacade(exchange).getId());
            claimDataRepository.save(claimData);
            writeInfoClaimLogMessage(StatusProcessingType.CLAIM_RAW_DATA_UNMARSHALLED, exchange);

            var claim = ClaimFactory.claimFacade(exchange);

            claimRepository.save(claim);
            writeInfoClaimLogMessage(StatusProcessingType.CLAIM_EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logIllegalArgumentException(final Exchange exchange) {

        try {
            ClaimLog claimLog = (ClaimLog) ClaimFactory.configureEntity(new ClaimLog(), exchange);
            claimLog.setMessageTyp(MessageType.ERROR);
            var message = LogServiceError.getMessage(exchange);
            claimLog.setMessage(BindyIllegalArgumentMessageEnricher.enrich(message, ImportClaimData.class));
            var stack = LogServiceError.getStack(exchange);
            claimLog.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
            claimLogRepository.save(claimLog);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logHttpOperationFailedException(final Exchange exchange) {

        try {
            ClaimLog claimLog = (ClaimLog) ClaimFactory.configureEntity(new ClaimLog(), exchange);
            claimLog.setMessageTyp(MessageType.ERROR);
            var responseBody = ((HttpOperationFailedException) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT)).getResponseBody();
            claimLog.setMessage(LogServiceError.getMessage(exchange).concat(" (" + responseBody + ")"));
            var stack = LogServiceError.getStack(exchange);
            claimLog.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
            claimLogRepository.save(claimLog);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logContent(final Exchange exchange) {

        try {
            ContentContainer contentContainer = exchange.getMessage().getBody(ClaimContentWrapper.class).getContentContainer();
            ClaimContent claimContent = (ClaimContent) ClaimFactory.configureEntity(new ClaimContent(), exchange);
            ObjectMapper mapper = mapperBuilder.build();
            claimContent.setJson(mapper.writeValueAsString(contentContainer));
            claimContentRepository.save(claimContent);
            writeInfoClaimLogMessage(StatusProcessingType.CLAIM_CONTENT_DATA_CREATED, exchange);

        } catch (JsonProcessingException e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    public void logXml(final Exchange exchange) {

        try {
            String xml = exchange.getMessage().getBody(ClaimContentWrapper.class).getXjustizXml();
            ClaimXml claimXml = (ClaimXml) ClaimFactory.configureEntity(new ClaimXml(), exchange);
            claimXml.setXjustizVersion(getXjustizVersion());
            claimXml.setContent(xml);
            claimXmlRepository.save(claimXml);
            writeInfoClaimLogMessage(StatusProcessingType.CLAIM_XJUSTIZ_MESSAGE_CREATED, exchange);

            var entryEntity = ClaimFactory.claimFacade(exchange);
            NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 justiz0500010 = XmlUnmarshaller
                    .unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(xml);
            entryEntity.setEhUuid(UUID.fromString(justiz0500010.getNachrichtenkopf().getEigeneNachrichtenID()));
            claimRepository.save(entryEntity);
            writeInfoClaimLogMessage(StatusProcessingType.CLAIM_EH_UUID_UPDATED, exchange);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

    private void writeInfoClaimLogMessage(StatusProcessingType processingType, Exchange exchange) {
        writeGenericClaimLogMessage(processingType, MessageType.INFO, exchange);
    }

    public void writeGenericClaimLogMessage(StatusProcessingType processingType, MessageType msgType, Exchange exchange) {
        try {
            ClaimLog claimLog = (ClaimLog) ClaimFactory.configureEntity(new ClaimLog(), exchange);
            claimLog.setMessageTyp(msgType);
            claimLog.setMessage(processingType.name());
            claimLog.setComment(processingType.getDescriptor());
            claimLogRepository.save(claimLog);
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }
    }

}
