package de.muenchen.eh.claim.efile;

import lombok.Getter;

@Getter
public enum DocumentName {

    ANTRAG("EH-Antrag"),
    BESCHEID("EH-Bescheid"),
    KOSTEN("EH-Kostenbescheid"),
    VERWERFUNG("EH-Verwerfungsbescheid"),
    VERFAHRENSMITTEILUNG("EH-Verfahrensmitteilung");

    private final String descriptor;

    DocumentName(String descriptor) {
        this.descriptor = descriptor;
    }

    public static DocumentName fromDescriptor(String descriptor) {
        for (DocumentName name : values()) {
            if (name.descriptor.equals(descriptor)) {
                return name;
            }
        }
        throw new IllegalArgumentException("Unknown descriptor: " + descriptor);
    }
}
