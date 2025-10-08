package de.muenchen.eh.kvue.claim.efile;

import de.muenchen.eh.kvue.claim.efile.operation.document.AddCaseFile;
import de.muenchen.eh.kvue.claim.efile.operation.document.CheckCaseFiles;
import de.muenchen.eh.kvue.claim.efile.operation.document.EfileRecord;
import de.muenchen.eh.kvue.claim.efile.operation.document.FindCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class EfilesOperationExecutor implements Processor {

    private final FindCollection collectionFinder;
    private final AddCaseFile addCaseFile;
    private final CheckCaseFiles checkCaseFiles;

    @Override
    public void process(Exchange exchange) throws Exception {

        EfileRecord operation = new EfileRecord(collectionFinder, addCaseFile, checkCaseFiles);
        operation.execute(exchange);

    }



}
