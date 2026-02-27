package de.muenchen.eh.claim.efile;

import lombok.Getter;

@Getter
public enum DocumentName {

    ANTRAG("EH-Antrag", "EH-Antrag.pdf"),
    BESCHEID("Bussgeldbescheid", "Bussgeldbescheid.pdf"),
    KOSTEN("Kostenbescheid", "Kostenbescheid.pdf"),
    VERWERFUNG("Verwerfungsbescheid", "Verwerfungsbescheid.pdf"),
    VERFAHRENSMITTEILUNG("EH-Verfahrensmitteilung", "EH-Verfahrensmitteilung.xml");

    private final String descriptor;
    private final String fullName;

    DocumentName(String descriptor, String fullName) {
        this.descriptor = descriptor;
        this.fullName = fullName;
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
