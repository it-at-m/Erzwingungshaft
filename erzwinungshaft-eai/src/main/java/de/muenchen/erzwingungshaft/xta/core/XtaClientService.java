package de.muenchen.erzwingungshaft.xta.core;

import de.muenchen.erzwingungshaft.xta.config.XtaClientConfig;
import de.muenchen.erzwingungshaft.xta.dto.XtaIdentifier;
import de.muenchen.erzwingungshaft.xta.dto.XtaMessage;
import de.muenchen.erzwingungshaft.xta.dto.XtaMessageMetaData;
import de.muenchen.erzwingungshaft.xta.dto.XtaStatusListing;
import de.muenchen.erzwingungshaft.xta.exception.XtaClientRuntimeException;
import de.muenchen.erzwingungshaft.xta.mapper.ResponseMapper;
import genv3.de.xoev.transport.xta.x211.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XtaClientService {

    private final XtaServiceWrapper xtaServiceWrapper;
    private final ResponseMapper responseMapper;
    private final XtaClientConfig xtaClientConfig;

    public boolean isServiceAvailable(XtaMessageMetaData xtaMessageMetaData) {
        try {
            return responseMapper
                    .isServiceAvailable(xtaServiceWrapper
                            .lookupService("", null, null));
        } catch (XTAWSTechnicalProblemException | PermissionDeniedException | ParameterIsNotValidException e) {
            log.warn("Service is not available", e);
            return false;
        }
    }

    public boolean isAccountActive(XtaIdentifier xtaIdentifier)  {
        try {
            xtaServiceWrapper.checkAccountActive(xtaIdentifier);
            return true;
        } catch (XTAWSTechnicalProblemException e) {
            throw new XtaClientRuntimeException("Failed to check if account is active.", e);
        } catch ( PermissionDeniedException e) {
            log.warn("Permission denied - account not active.", e);
            return false;
        }
    }

    public XtaStatusListing getStatusList(XtaIdentifier xtaIdentifier)  {
        try {
            return xtaServiceWrapper.getStatusList(xtaIdentifier, xtaClientConfig.getMaxListItems());
        } catch (XTAWSTechnicalProblemException | PermissionDeniedException e) {
            throw new XtaClientRuntimeException("Failed to receive list of status.", e);
        }
    }

    private XtaMessage enrichXtaMessageWithID(XtaMessage messageWithoutId) {
        XtaIdentifier xtaIdentifier = messageWithoutId.metaData().authorIdentifier();

        try {
            String messageId = xtaServiceWrapper.createMessageId(xtaIdentifier);
            return messageWithoutId.withMetaData(messageWithoutId.metaData().withMessageId(messageId));
        } catch (XTAWSTechnicalProblemException | PermissionDeniedException e) {
            throw new XtaClientRuntimeException("Failed to create message with Id.", e);
        }
    }

    public void sendMessage(XtaMessage xtaMessage)  {
        if (xtaMessage.metaData().messageId() == null || xtaMessage.metaData().messageId().isEmpty()) {
            log.debug("No message Id present");
            xtaMessage = enrichXtaMessageWithID(xtaMessage);
        }

        try {
            xtaServiceWrapper.sendMessage(xtaMessage, null);
        } catch (SyncAsyncException | ParameterIsNotValidException | PermissionDeniedException | MessageSchemaViolationException | MessageVirusDetectionException | XTAWSTechnicalProblemException e) {
            throw new XtaClientRuntimeException("Failed to send message.", e);
        }
    }

    public Optional<XtaMessage> getMessage(XtaMessageMetaData xtaMessageMetaData)  {
        try {
            return Optional.of(xtaServiceWrapper.getMessage(xtaMessageMetaData.messageId(), xtaMessageMetaData.readerIdentifier()));
        } catch (XTAWSTechnicalProblemException | PermissionDeniedException | InvalidMessageIDException e) {
            throw new XtaClientRuntimeException("Failed to fetch message.", e); // TODO add identifier and messageID
        }
    }






}
