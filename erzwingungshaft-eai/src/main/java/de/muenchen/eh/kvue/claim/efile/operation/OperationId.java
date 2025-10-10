package de.muenchen.eh.kvue.claim.efile.operation;

import lombok.Getter;

@Getter
public enum OperationId {

    READ_CASE_FILE_COLLECTIONS("ReadApentry"),
    CREATE_FILE("CreateFile"),
    CREATE_FINE("CreateProcedure"),
    CREATE_OUTGOING("CreateOutgoing"),
    CREATE_CONTENT_OBJECT("CreateContentObject"),;

    private final String descriptor;

    OperationId(String descriptor) {
        this.descriptor = descriptor;
    }
}
