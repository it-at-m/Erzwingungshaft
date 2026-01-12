package de.muenchen.eh.claim.xta.transport.metadata;

import eu.osci.ws._2014._10.transport.QualifierType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QualifierBuilder {

    private String subject;
    private String service;
    private MessageTypeBuilder messageType;

    @Getter
    @Builder
    public static class MessageTypeBuilder {
        private String listURI;
        private String code;
        private String listVersionID;
        private String payloadSchema;

        public QualifierType.MessageType build() {
            QualifierType.MessageType t = new QualifierType.MessageType();
            t.setListURI(listURI);
            t.setCode(code);
            t.setListVersionID(listVersionID);
            t.setPayloadSchema(payloadSchema);
            return t;
        }
    }

    public QualifierType buildJaxb() {
        QualifierType q = new QualifierType();
        q.setSubject(subject);
        q.setService(service);
        if (messageType != null) {
            q.setMessageType(messageType.build());
        }
        return q;
    }
}
