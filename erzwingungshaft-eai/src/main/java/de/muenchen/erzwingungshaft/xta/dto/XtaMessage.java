package de.muenchen.erzwingungshaft.xta.dto;

import genv3.de.xoev.transport.xta.x211.ContentType;
import java.util.List;
import lombok.With;

public record XtaMessage(

        // Holds adress
        @With XtaMessageMetaData metaData,

        // message url
        ContentType messageFile,

        // optional attachments
        List<ContentType> attachmentFiles) {

}
