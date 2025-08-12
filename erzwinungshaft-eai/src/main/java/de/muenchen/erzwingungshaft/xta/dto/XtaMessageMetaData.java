package de.muenchen.erzwingungshaft.xta.dto;

public record XtaMessageMetaData(
        String messageId,
        String listURI,
        String listVersionID,
        String payloadSchema
) {
}
