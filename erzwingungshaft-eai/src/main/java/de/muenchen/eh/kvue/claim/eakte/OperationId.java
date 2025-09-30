package de.muenchen.eh.kvue.claim.eakte;

import lombok.Getter;

@Getter
public enum OperationId {

    READ_APENTRY("ReadApentry");

    private final String descriptor;

    OperationId(String descriptor) {
        this.descriptor = descriptor;
    }
}
