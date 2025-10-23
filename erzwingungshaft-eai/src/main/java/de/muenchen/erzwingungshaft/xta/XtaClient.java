package de.muenchen.erzwingungshaft.xta;

import de.muenchen.erzwingungshaft.xta.config.XtaClientConfig;
import de.muenchen.erzwingungshaft.xta.core.XtaClientService;
import de.muenchen.erzwingungshaft.xta.dto.*;
import de.muenchen.erzwingungshaft.xta.exception.XtaClientRuntimeException;
import genv3.de.xoev.transport.xta.x211.XTAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XtaClient {

    private final XtaClientService xtaClientService;
    private final XtaClientConfig xtaClientConfig;

    public void sendMessage(XtaMessage message) {
        log.debug("sendMessage");
        // lookup service
        checkIfServiceIsAvailable(message.metaData());
        log.debug("XTAClient - Service available({})", message.metaData());
        // isAccountActive
        checkIfAccountIsActive(message.metaData().authorIdentifier());
        log.debug("XTAClient - Account active ({})", message.metaData().authorIdentifier());
        // sendMsg
        xtaClientService.sendMessage(message);
        log.debug("XTAClient - sendMessage({})", message);
    }














/*

    public void fetchMessages() {
        log.debug("fetchMessages");
        Consumer<XtaMessage> processMessage = null;

        // get all active clientIdentifiers
        List<XtaIdentifier> clientIdentifiers = xtaClientConfig.getClientIdentifiers();
        clientIdentifiers = clientIdentifiers.stream().filter(xtaClientService::isAccountActive).toList();

        List<XtaFetchMessageMetaData> mappedClientIdentifiers = clientIdentifiers.stream().map(clientIdentifier -> new XtaFetchMessageMetaData(clientIdentifier, processMessage, Collections.emptySet())).toList();

        // fetch messages for each clientIdentifier
        List<XtaStatusListing> xtaStatusListings = clientIdentifiers.stream().map(xtaClientService::getStatusList).toList();

        xtaStatusListings.stream().map(xtaStatusListing -> {
            // fetchMessages
            xtaClientService.getMessage(null);
           // magic


            return null;
        });

        // FlatMap into one list


    }

    public void fetchMessagesFrom(XtaIdentifier clientIdentifier) {
        // check if active
        checkIfAccountIsActive(clientIdentifier);
        //
        XtaFetchMessageMetaData xtaFetchMessageMetaData = new XtaFetchMessageMetaData(clientIdentifier, null, Collections.emptySet());
        XtaStatusListing xtaStatusListing = xtaClientService.getStatusList(clientIdentifier);

        List<XtaMessageMetaData> metas = xtaStatusListing.messages().stream()
                .filter(meta -> shouldMessageBeFetched(meta, xtaFetchMessageMetaData))
                .toList();


        for (XtaMessageMetaData meta : metas) {
            Optional<XtaMessage> maybeMsg = xtaClientService.getMessage(meta);
            if (maybeMsg.isEmpty()) {
                continue;
            };
            XtaMessage msg = maybeMsg.get();

            try {
                // fachlich verarbeiten
                processMessage.accept(msg);
                // schließen (Quittierung)
                xtaClientService.closeMessage(msg);
            } catch (RuntimeException ex) {
                log.error("Processing of {} failed, not closing", meta.messageId(), ex);
            }

            // Transportprotokoll abrufen
            xtaClientService.getTransportReport(meta)
                    .ifPresent(report -> collectedReports.add(report));
        }





    }

    public void fetchMessagesFrom(XtaIdentifier clientIdentifier, Consumer<XtaMessage> processMessage) {
        checkIfAccountIsActive(clientIdentifier);

        XtaFetchMessageMetaData param = new XtaFetchMessageMetaData(clientIdentifier, processMessage, Collections.emptySet());
        XtaStatusListing listing = xtaClientService.getStatusList(clientIdentifier);

        List<XtaTransportReport> reports = new ArrayList<>();

        for (XtaMessageMetaData meta : listing.messages()) {
            if (!shouldMessageBeFetched(meta, param)) continue;

            Optional<XtaMessage> maybeMsg = xtaClientService.getMessage(meta);
            if (maybeMsg.isEmpty()) continue;

            XtaMessage msg = maybeMsg.get();
            try {
                processMessage.accept(msg);
                xtaClientService.closeMessage(msg);
            } catch (RuntimeException ex) {
                log.error("Processing of message '{}' failed! Not closing.", meta.messageId(), ex);
            }

            xtaClientService.getTransportReport(meta).ifPresent(reports::add);
        }

        return null;
    }
*/





    private boolean shouldMessageBeFetched(XtaMessageMetaData messageMetaData, XtaFetchMessageMetaData parameter) {
        return !parameter.isMessageViewed(messageMetaData) && isMessageSupported(messageMetaData);
    }

    private boolean isMessageSupported(XtaMessageMetaData xtaMessageMetaData) {
        Predicate<XtaMessageMetaData> predicate = xtaClientConfig.getMessageMetaDataFilter();
        return predicate != null && predicate.test(xtaMessageMetaData);
    }

    private void checkIfAccountIsActive(XtaIdentifier xtaIdentifier) {
        if (!xtaClientService.isAccountActive(xtaIdentifier)) {
            throw new XtaClientRuntimeException("Account is not active");
        }
    }

    private void checkIfServiceIsAvailable(XtaMessageMetaData xtaMessageMetaData) {
        if (!xtaClientService.isServiceAvailable(xtaMessageMetaData)) {
            throw new XtaClientRuntimeException("Service is not available");
        }
    }



}
