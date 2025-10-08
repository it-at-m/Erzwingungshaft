package de.muenchen.eh.kvue.claim.efile.operation.document;

import de.muenchen.eh.kvue.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.kvue.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.db.LogServiceClaim;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

@RequiredArgsConstructor
abstract class EfileOperation {

    @Produce(value= EfileRouteBuilder.DMS_CONNECTION)
    protected ProducerTemplate eakteConnector;

    protected final OperationIdFactory operationIdFactory;

    protected final LogServiceClaim logServiceClaim;

    protected abstract void execute(Exchange exchange);

}
