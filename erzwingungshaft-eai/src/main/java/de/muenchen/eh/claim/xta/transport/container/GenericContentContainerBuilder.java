package de.muenchen.eh.claim.xta.transport.container;

import de.xoev.transport.xta._211.GenericContentContainer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenericContentContainerBuilder {

    private ContentContainerBuilder contentContainer;

    public GenericContentContainer buildContainer() {

        de.xoev.transport.xta._211.GenericContentContainer gcc = new de.xoev.transport.xta._211.GenericContentContainer();

        if (contentContainer != null) {
            gcc.setContentContainer(contentContainer.build());
        }

        return gcc;
    }
}
