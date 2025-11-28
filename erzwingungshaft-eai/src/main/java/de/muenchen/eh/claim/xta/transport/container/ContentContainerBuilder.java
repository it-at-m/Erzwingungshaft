package de.muenchen.eh.claim.xta.transport.container;

import genv3.de.xoev.transport.xta.x211.ContentType;
import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

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
