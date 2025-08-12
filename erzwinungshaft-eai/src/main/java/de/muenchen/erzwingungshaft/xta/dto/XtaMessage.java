package de.muenchen.erzwingungshaft.xta.dto;

import java.util.List;

public record XtaMessage (

        // Holds adress
        XtaMessageMetaData metaData,

        // message content
        XtaFile messageFile,

        // optional attachments
        List<XtaFile> attachmentFiles
){

}
