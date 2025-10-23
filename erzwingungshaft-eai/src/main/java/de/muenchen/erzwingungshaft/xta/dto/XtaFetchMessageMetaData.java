package de.muenchen.erzwingungshaft.xta.dto;

import java.util.Set;
import java.util.function.Consumer;

public record XtaFetchMessageMetaData(
        XtaIdentifier xtaIdentifier,
        Consumer<XtaMessage> processMessage,
        Set<String> viewedMessageIds
) {


    public boolean isMessageViewed(XtaMessageMetaData xtaMessageMetaData) {
        return viewedMessageIds.contains(xtaMessageMetaData.messageId());
    }

}
