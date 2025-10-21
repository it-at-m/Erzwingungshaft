package de.muenchen.eh.kvue.claim.efile.operation.contentbuilder;

import de.muenchen.eh.kvue.claim.efile.DocumentName;
import de.muenchen.eh.log.DocumentType;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OutgoingRequestBodyDTOBuilder {

    private final Optional<List<ClaimDocument>> documents;

    public static OutgoingRequestBodyDTOBuilder create(Optional<List<ClaimDocument>> documents) {
        return new OutgoingRequestBodyDTOBuilder(documents);
    }


    public List<File> build() throws IOException {

        List<File> dataHandlers = new ArrayList<>();

        if (documents.isPresent()) {
            for (ClaimDocument document : documents.get()) {
                var prefix = document.getDocumentType().equals(DocumentType.ANTRAG.getDescriptor()) ? DocumentName.ANTRAG.getDescriptor() : DocumentName.BESCHEID.getDescriptor();
                var suffix = FilenameUtils.getExtension(document.getFileName());
                var newFilePath = Path.of(prefix.concat(".").concat(suffix));

                Files.deleteIfExists(newFilePath);

                Path tempFile = Files.createFile(newFilePath);
                Files.write(tempFile, document.getDocument());
                dataHandlers.add(new File(tempFile.toUri()));

            }
        }
        return dataHandlers;
    }

}
