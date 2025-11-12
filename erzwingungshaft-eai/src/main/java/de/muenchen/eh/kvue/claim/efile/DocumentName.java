package de.muenchen.eh.kvue.claim.efile;

import lombok.Getter;

@Getter
public enum DocumentName {

    ANTRAG("EH-Antrag"),
    BESCHEID("EH-Bescheid");

    private final String descriptor;

    DocumentName(String descriptor) {
        this.descriptor = descriptor;
    }
}
