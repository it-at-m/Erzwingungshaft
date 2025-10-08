package de.muenchen.eh.kvue.claim.efile.operation.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanUp {

    private final FindCollection collectionFinder;
    private final CheckCaseFiles checkCaseFiles;

    public void clearCache() {
        collectionFinder.clearApentryCache();
        checkCaseFiles.clearApentryCache();
    }

}
