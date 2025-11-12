package de.muenchen.eh.xta.core;

import de.muenchen.eh.xta.config.XtaClientConfig;
import de.muenchen.eh.xta.dto.XtaIdentifier;
import de.muenchen.eh.xta.dto.XtaMessage;
import de.muenchen.eh.xta.dto.XtaMessageMetaData;
import de.muenchen.eh.xta.dto.XtaStatusListing;
import de.muenchen.eh.xta.exception.XtaClientRuntimeException;
import de.muenchen.eh.xta.mapper.ResponseMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean isAccountActive(XtaIdentifier xtaIdentifier) {
        try {
            xtaServiceWrapper.checkAccountActive(xtaIdentifier);
            return true;
        } catch (XTAWSTechnicalProblemException e) {
            throw new XtaClientRuntimeException("Failed to check if account is active.", e);
        } catch (PermissionDeniedException e) {
            log.warn("Permission denied - account not active.", e);
            return false;
        }
    }

    public XtaStatusListing getStatusList(XtaIdentifier xtaIdentifier) {
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

    public void sendMessage(XtaMessage xtaMessage) {
        if (xtaMessage.metaData().messageId() == null || xtaMessage.metaData().messageId().isEmpty()) {
            log.debug("No message Id present");
            xtaMessage = enrichXtaMessageWithID(xtaMessage);
        }

        try {
            xtaServiceWrapper.sendMessage(xtaMessage, null);
        } catch (SyncAsyncException | ParameterIsNotValidException | PermissionDeniedException | MessageSchemaViolationException
                | MessageVirusDetectionException | XTAWSTechnicalProblemException e) {
            throw new XtaClientRuntimeException("Failed to send message.", e);
        }
    }

    public Optional<XtaMessage> getMessage(XtaMessageMetaData xtaMessageMetaData) {
        try {
            return Optional.of(xtaServiceWrapper.getMessage(xtaMessageMetaData.messageId(), xtaMessageMetaData.readerIdentifier()));
        } catch (XTAWSTechnicalProblemException | PermissionDeniedException | InvalidMessageIDException e) {
            throw new XtaClientRuntimeException("Failed to fetch message.", e); // TODO add identifier and messageID
        }
    }

}
