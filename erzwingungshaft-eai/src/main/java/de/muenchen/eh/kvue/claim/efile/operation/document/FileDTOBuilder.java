package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class FileDTOBuilder {

    private final ClaimProcessingContentWrapper contentWrapper;

    CreateFileDTO createFileDTO = new CreateFileDTO();

    public FileDTOBuilder einzelakte() {

        Optional<Objektreferenz> apentryCollection = Optional.of((Objektreferenz) contentWrapper.getEakte().get(OperationId.READ_APENTRY_COLLECTION.name()));
        apentryCollection.ifPresent(ac -> {
            ReadApentryAntwortDTO apentryCaseFiles = (ReadApentryAntwortDTO) contentWrapper.getEakte().get(OperationId.READ_APENTRY_CASE_FILES.name());
            var caseFileName = prefix(ac.getObjname()).concat("-").concat(contentWrapper.getClaimImport().getOutputDirectory());
            var count = apentryCaseFiles.getGiobjecttype() != null ? apentryCaseFiles.getGiobjecttype().stream().filter(o -> o.getObjname().equals(caseFileName)).count() : 0;
            createFileDTO.setShortname(prefix(ac.getObjname()).concat("-").concat(contentWrapper.getClaimImport().getOutputDirectory()).concat("-").concat(String.valueOf(++count)));
            createFileDTO.setApentry(ac.getObjaddress());
        });

        return this;
    }


    public CreateFileDTO build() {
        return createFileDTO;
    }

    private String prefix(String value) {
        String[] parts = value.split("/");
        return parts[0];
    }

}
