package de.muenchen.eh.claim.xta.transport.container;

import genv3.de.xoev.transport.xta.x211.ContentType;
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

    public ContentType build() {

        ContentType ct =  new ContentType();

        ct.setContentType(contentType);
        ct.setEncoding(encoding);
        ct.setFilename(filename);
        ct.setContentDescription(contentDescription);
        ct.setValue(value);

        return ct;
    }
}

