package de.muenchen.eh.kvue.claim.efile.operation.contentbuilder;

import com.sun.istack.ByteArrayDataSource;
import de.muenchen.eh.kvue.claim.efile.DocumentName;
import de.muenchen.eh.log.DocumentType;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

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
                var prefix = document.getDocumentType().equals(DocumentType.ANTRAG.getDescriptor()) ? DocumentName.ANTRAG.getDescriptor()
                        : DocumentName.BESCHEID.getDescriptor();
                var suffix = FilenameUtils.getExtension(document.getFileName());

                DataSource dataSource = new ByteArrayDataSource(document.getDocument(), document.getDocument().length, "application/octet-stream");
                dataHandlers.put(prefix.concat(".").concat(suffix), new DataHandler(dataSource));

            }
        }
        return dataHandlers;
    }

}
