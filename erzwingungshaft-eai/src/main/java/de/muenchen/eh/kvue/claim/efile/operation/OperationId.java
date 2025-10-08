package de.muenchen.eh.kvue.claim.efile.operation;

import lombok.Getter;

@Getter
public enum OperationId {

    READ_APENTRY_COLLECTION("ReadApentry"),
    READ_APENTRY_CASE_FILES("ReadApentry"),
    CREATE_FILE("CreateFile");

    private final String descriptor;

    OperationId(String descriptor) {
        this.descriptor = descriptor;
    }
}
