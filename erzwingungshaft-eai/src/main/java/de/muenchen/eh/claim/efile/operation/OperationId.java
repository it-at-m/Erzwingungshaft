package de.muenchen.eh.claim.efile.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eakte.api.rest.model.CreateContentObjectAntwortDTO;
import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eakte.api.rest.model.SearchFileResponseDTO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;

@Getter
public enum OperationId {

    READ_COLLECTIONS("ReadApentry", new TypeReference<ReadApentryAntwortDTO>() {
    }),
    SEARCH_FILE("SearchFile", new TypeReference<SearchFileResponseDTO>() {
    }),
    CREATE_FILE("CreateFile", new TypeReference<DmsObjektResponse>() {
    }),
    CREATE_FINE("CreateProcedure", new TypeReference<DmsObjektResponse>() {
    }),
    UPDATE_SUBJECT_DATA_FILE("UpdateBusinessDataValue", new TypeReference<Map<String, Object>>() {
    }),
    UPDATE_SUBJECT_DATA_FINE("UpdateBusinessDataValue", new TypeReference<Map<String, Object>>() {
    }),
    CREATE_OUTGOING("CreateOutgoing", new TypeReference<CreateOutgoingAntwortDTO>() {
    }),
    CREATE_CONTENT_OBJECT("CreateContentObject", new TypeReference<CreateContentObjectAntwortDTO>() {
    });

    private final String descriptor;
    private final TypeReference<?> typeRef;

    OperationId(String descriptor, TypeReference<?> typeRef) {

        this.descriptor = descriptor;
        this.typeRef = typeRef;
    }

    public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
        return mapper.readValue(json, typeRef);
    }

    public static OperationId fromDescriptor(String descriptor) {
        return Arrays.stream(values())
                .filter(op -> op.descriptor.equals(descriptor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown openapi.operationId : ".concat(descriptor)));
    }
}
