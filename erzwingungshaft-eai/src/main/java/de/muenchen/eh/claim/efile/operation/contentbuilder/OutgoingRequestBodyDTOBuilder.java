package de.muenchen.eh.claim.efile.operation.contentbuilder;

import com.sun.istack.ByteArrayDataSource;
import de.muenchen.eh.claim.efile.DocumentName;
import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.log.DocumentType;
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
    
    private static final Map<String, String> DOCUMENT_TYPE_TO_PREFIX = Map.of(
    		        DocumentType.ANTRAG.getDescriptor(), DocumentName.ANTRAG.getDescriptor(),
    		        DocumentType.BESCHEID.getDescriptor(), DocumentName.BESCHEID.getDescriptor(),
    		        DocumentType.KOSTEN.getDescriptor(), DocumentType.KOSTEN.getDescriptor(),
    		        DocumentType.VERWERFUNG.getDescriptor(), DocumentType.VERWERFUNG.getDescriptor()
    		    );

    public static OutgoingRequestBodyDTOBuilder create(List<ClaimDocument> documents) {
        return new OutgoingRequestBodyDTOBuilder(documents);
    }

    public Map<String, DataHandler> build() throws IOException {

        Map<String, DataHandler> dataHandlers = new TreeMap<>();

        if (documents != null) {
            for (ClaimDocument document : documents) {

                String prefix = DOCUMENT_TYPE_TO_PREFIX.getOrDefault(document.getDocumentType(), "unknown");
                
                var suffix = FilenameUtils.getExtension(document.getFileName());

                DataSource dataSource = new ByteArrayDataSource(document.getDocument(), document.getDocument().length, "application/octet-stream");
                dataHandlers.put(prefix.concat(".").concat(suffix), new DataHandler(dataSource));

            }
        }
        return dataHandlers;
    }

}
