package de.muenchen.eh.claim.xta.transport.container;

import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenericContentContainerBuilder {

    private ContentContainerBuilder contentContainer;

    public GenericContentContainer buildContainer() {

        GenericContentContainer gcc = new GenericContentContainer();

        if (contentContainer != null) {
            gcc.setContentContainer(contentContainer.build());
        }

        return gcc;
    }
}
