package de.muenchen.eh.xta;

import de.muenchen.eh.xta.config.XtaClientConfig;
import de.muenchen.eh.xta.dto.XtaMessage;
import de.muenchen.eh.xta.dto.XtaMessageMetaData;
import de.muenchen.eh.xta.mapper.XtaHelper;
import genv3.de.xoev.transport.xta.x211.ContentType;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XtaClientWrapper {

    private final XtaClient xtaClient;

    private final XtaClientConfig xtaClientConfig;

    public void sendMessage(String message, List<String> attachments) throws IOException {
        // create some message
        List<ContentType> dummyAttachment = List.of(XtaCommandLineRunner.createDummyAttachment());
        ContentType xtaMessage = XtaHelper.createMainMessageFromString(message);

        // create some messageMetaData
        XtaMessageMetaData xtaMessageMetaData = XtaHelper.createMetaData(xtaClientConfig);

        // send message
        XtaMessage toSend = new XtaMessage(xtaMessageMetaData, xtaMessage, dummyAttachment);
        xtaClient.sendMessage(toSend);
    }
}
