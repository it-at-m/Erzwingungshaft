package de.muenchen.eh.claim.xta.transport.container;

import genv3.de.xoev.transport.xta.x211.ContentType;
import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import lombok.Builder;
import lombok.Singular;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentContainerBuilder {

    private ContentType message;
    @Singular
    private List<ContentType> attachments;

    public GenericContentContainer.ContentContainer build() {

        GenericContentContainer.ContentContainer cc = new GenericContentContainer.ContentContainer();

        if (message != null) {
            cc.setMessage(message);
        }

        if (attachments != null) {
            attachments.forEach(a -> cc.getAttachment().add(a));
        }

        return cc;
    }
}

