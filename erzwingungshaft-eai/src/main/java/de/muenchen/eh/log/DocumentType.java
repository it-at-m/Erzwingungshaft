package de.muenchen.eh.log;

import lombok.Getter;

@Getter
public enum DocumentType {

    ANTRAG("Antrag"),
    BESCHEID("Urbescheid");

    private final String descriptor;

    DocumentType(String descriptor) {
        this.descriptor = descriptor;
    }
}
