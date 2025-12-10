package de.muenchen.eh.claim.efile;

import lombok.Getter;

@Getter
public enum DocumentName {

    ANTRAG("EH-Antrag"),
    BESCHEID("EH-Bescheid"),
    KOSTEN("EH-Kostenbescheid"),
    VERWERFUNG("EH-Verwerfungsbescheid");

    private final String descriptor;

    DocumentName(String descriptor) {
        this.descriptor = descriptor;
    }
}
