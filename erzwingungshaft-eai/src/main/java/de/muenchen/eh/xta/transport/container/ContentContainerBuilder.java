package de.muenchen.eh.xta.transport.container;

import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import lombok.Builder;
import lombok.Singular;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentContainerBuilder {

    private ContentTypeBuilder message;

    @Singular
    private List<ContentTypeBuilder> attachments;

    public GenericContentContainer.ContentContainer build() {

        var cc = new GenericContentContainer.ContentContainer();

        if (message != null) {
            cc.setMessage(message.build());
        }

        if (attachments != null) {
            attachments.forEach(a -> cc.getAttachment().add(a.build()));
        }

        return cc;
    }
}

