package de.muenchen.eh.log;

import lombok.Getter;

@Getter
public enum DocumentType {

    ANTRAG("Antrag"),
    BESCHEID("Urbescheid"),
    VERWERFUNG("Verwerfung"),
    KOSTEN("Kosten");

    private final String descriptor;

    DocumentType(String descriptor) {
        this.descriptor = descriptor;
    }

    public static DocumentType fromDescriptor(String descriptor) {
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.descriptor.equals(descriptor))
                return documentType;
        }
        throw new IllegalArgumentException("No such documentType: " + descriptor);
    }

}
