package de.muenchen.eh.claim.efile.operation.contentbuilder;

import com.sun.istack.ByteArrayDataSource;
import de.muenchen.eh.common.FileNameUtils;
import de.muenchen.eh.db.entity.ClaimDocument;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutgoingRequestBodyDTOBuilder {

    private final List<ClaimDocument> documents;

    public static OutgoingRequestBodyDTOBuilder create(List<ClaimDocument> documents) {
        return new OutgoingRequestBodyDTOBuilder(documents);
    }

    public Map<String, DataHandler> build() throws IOException {

        Map<String, DataHandler> dataHandlers = new TreeMap<>();

        if (documents != null) {
            for (ClaimDocument document : documents) {

                DataSource dataSource = new ByteArrayDataSource(document.getDocument(), document.getDocument().length, "application/octet-stream");
                dataHandlers.put(FileNameUtils.toHumanReadableFileName(document.getFileName()), new DataHandler(dataSource));

            }
        }
        return dataHandlers;
    }

}
