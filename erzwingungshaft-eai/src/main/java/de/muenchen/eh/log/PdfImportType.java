package de.muenchen.eh.log;

import lombok.Getter;

@Getter
public enum PdfImportType {

    ANTRAG("Antrag"),
    BESCHEID("Urbescheid");

    private final String descriptor;

    PdfImportType(String descriptor) {
        this.descriptor = descriptor;
    }
}
