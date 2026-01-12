package de.muenchen.eh.claim.xta.transport.container;

import jakarta.activation.DataHandler;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentTypeBuilder {

    private String contentType;
    private String encoding;
    private String filename;
    private String contentDescription;
    private DataHandler value;

    public de.xoev.transport.xta._211.ContentType build() {

        de.xoev.transport.xta._211.ContentType ct = new de.xoev.transport.xta._211.ContentType();

        ct.setContentType(contentType);
        ct.setEncoding(encoding);
        ct.setFilename(filename);
        ct.setContentDescription(contentDescription);
        ct.setValue(value);

        return ct;
    }
}
