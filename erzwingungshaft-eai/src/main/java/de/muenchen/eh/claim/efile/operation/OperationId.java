package de.muenchen.eh.claim.efile.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.eakte.api.rest.model.CreateContentObjectAntwortDTO;
import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;

@Getter
public enum OperationId {

    READ_COLLECTIONS("ReadApentry") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, ReadApentryAntwortDTO.class);
        }
    },
    CREATE_FILE("CreateFile") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, DmsObjektResponse.class);
        }
    },
    CREATE_FINE("CreateProcedure") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, DmsObjektResponse.class);
        }
    },
    UPDATE_SUBJECT_DATA_FILE("UpdateBusinessDataValue") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, Map.class);
        }
    },
    UPDATE_SUBJECT_DATA_FINE("UpdateBusinessDataValue") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, Map.class);
        }
    },
    CREATE_OUTGOING("CreateOutgoing") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, CreateOutgoingAntwortDTO.class);
        }
    },
    CREATE_CONTENT_OBJECT("CreateContentObject") {
        @Override
        public Object parseResponse(String json, ObjectMapper mapper) throws IOException {
            return mapper.readValue(json, CreateContentObjectAntwortDTO.class);
        }
    };

    private final String descriptor;

    OperationId(String descriptor) {
        this.descriptor = descriptor;
    }

    public abstract Object parseResponse(String json, ObjectMapper mapper) throws IOException;

    public static OperationId fromDescriptor(String descriptor) {
        return Arrays.stream(values())
                .filter(op -> op.descriptor.equals(descriptor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown openapi.operationId : ".concat(descriptor)));
    }
}
