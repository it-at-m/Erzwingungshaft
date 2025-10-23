package de.muenchen.erzwingungshaft.xta.mapper;


import de.muenchen.erzwingungshaft.xta.config.XtaClientConfig;
import de.muenchen.erzwingungshaft.xta.dto.XtaMessageMetaData;
import genv3.de.xoev.transport.xta.x211.ContentType;
import jakarta.activation.DataHandler;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.attachment.ByteDataSource;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class XtaHelper {

    public static XtaMessageMetaData createMetaData(XtaClientConfig config) {
        return new XtaMessageMetaData("", config.getBusinessScenario(), config.getMessageType(), "", null, null, null, null, null);
    }

    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

    public static ContentType createMainMessageFromString(String messageContent) {
        byte[] message = messageContent.getBytes();
        DataHandler dh = new DataHandler(new ByteDataSource(message));

        ContentType contentType = new ContentType();
        contentType.setContentType("application/xml");
        contentType.setFilename("message.xml");
        contentType.setContentDescription("This message contains the main message.");
        contentType.setEncoding(StandardCharsets.UTF_8.name());
        contentType.setId(generateUUIDString());
        contentType.setLang("de");
        contentType.setSize(BigInteger.valueOf(message.length));
        contentType.setValue(dh);
        return contentType;
    }


}
