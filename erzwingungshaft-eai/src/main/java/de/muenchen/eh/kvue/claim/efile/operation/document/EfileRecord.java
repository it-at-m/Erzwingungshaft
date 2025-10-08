package de.muenchen.eh.kvue.claim.efile.operation.document;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;

@RequiredArgsConstructor
public class EfileRecord extends EfileRecordTemplate {

   private final FindCollection collectionFinder;
   private final AddCaseFile addCaseFile;
   private final CheckCaseFiles checkCaseFiles;

    @Override
    protected void findCollection(Exchange exchange) {
        collectionFinder.execute(exchange);
    }
    @Override
    protected void checkCaseFiles(Exchange exchange) {
        checkCaseFiles.execute(exchange);
    }
    @Override
    protected void addCaseFile(Exchange exchange) {
        addCaseFile.execute(exchange);
    }
    @Override
    protected void addBussgeldverfahren(Exchange exchange) {

    }
    @Override
    protected void addDocuments(Exchange exchange) {

    }
}
