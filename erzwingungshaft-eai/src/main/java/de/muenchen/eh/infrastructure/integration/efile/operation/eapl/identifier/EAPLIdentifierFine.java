package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.identifier;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EAPLIdentifierFine extends EAPLIdentifierTemplate {

    private final IdentifierCollection identifierCollection;
    private final IdentifierFile identifierFile;
    private final IdentifierFine identifierFine;

    @Override
    protected void findCollection(Exchange exchange) {
        identifierCollection.execute(exchange);
    }

    @Override
    protected void findFile(Exchange exchange) {
        identifierFile.execute(exchange);
    }

    @Override
    protected void addFine(Exchange exchange) {
        identifierFine.execute(exchange);
    }
}
