package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eakte.api.rest.model.CreateFileDTO;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.kvue.claim.efile.operation.OperationId;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class FileDTOBuilder {

    private final ClaimProcessingContentWrapper contentWrapper;

    private final CreateFileDTO createFileDTO = new CreateFileDTO();

    public static FileDTOBuilder create(ClaimProcessingContentWrapper contentWrapper) {
        return new FileDTOBuilder(contentWrapper);
    }

    public CreateFileDTO build() {

        return createCaseFileDTO();
    }

    private CreateFileDTO createCaseFileDTO() {

        Optional<Objektreferenz> collection = Optional.of((Objektreferenz) contentWrapper.getEfile().get(OperationId.READ_CASE_FILE_COLLECTIONS.name()));
        collection.ifPresent(ac -> {
            //       ReadApentryAntwortDTO apentryCaseFiles = (ReadApentryAntwortDTO) contentWrapper.getEakte().get(OperationId.READ_APENTRY_CASE_FILES.name());
            //        var caseFileName = prefix(ac.getObjname()).concat("-").concat(contentWrapper.getClaimImport().getOutputDirectory());
            //        var count = apentryCaseFiles.getGiobjecttype() != null ? apentryCaseFiles.getGiobjecttype().stream().filter(o -> o.getObjname().equals(caseFileName)).count() : 0;
            //        createFileDTO.setShortname(contentWrapper.getClaimImport().getOutputDirectory().concat("-").concat(String.valueOf(++count)));
            createFileDTO.setShortname(contentWrapper.getClaimImport().getGeschaeftspartnerId());
            createFileDTO.setApentry(ac.getObjaddress());
        });

        return createFileDTO;

    }

//    private String prefix(String value) {
//        String[] parts = value.split("/");
//        return parts[0];
//    }

}
