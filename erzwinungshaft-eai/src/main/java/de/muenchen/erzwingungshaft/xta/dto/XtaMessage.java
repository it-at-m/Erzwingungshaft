package de.muenchen.erzwingungshaft.xta.dto;

import genv3.de.xoev.transport.xta.x211.ContentType;
import lombok.With;

import java.util.List;

public record XtaMessage (

        // Holds adress
        @With XtaMessageMetaData metaData,

        // message url
        ContentType messageFile,

        // optional attachments
        List<ContentType> attachmentFiles
){

}
