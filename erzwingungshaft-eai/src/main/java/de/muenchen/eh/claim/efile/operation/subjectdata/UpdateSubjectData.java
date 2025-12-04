package de.muenchen.eh.claim.efile.operation.subjectdata;

import de.muenchen.eakte.api.rest.model.UpdateBusinessDataValueDTO;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.LogServiceClaim;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

abstract class UpdateSubjectData {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    protected final LogServiceClaim logServiceClaim;
    private Map<String, String> subjectDataValues;
    private final OperationIdFactory operationIdFactory;
    protected Exchange subjectExchange;

    public UpdateSubjectData(LogServiceClaim logServiceClaim, OperationIdFactory operationIdFactory) {

        this.logServiceClaim = logServiceClaim;
        this.operationIdFactory = operationIdFactory;

    }

    public Exchange execute(Exchange exchange, OperationId operationId) {

        this.subjectDataValues = subjectDataValuesBuilder(exchange);
        updateSubjectValues(exchange, operationId);
        return subjectExchange;

    }

    protected abstract Map<String, String> subjectDataValuesBuilder(Exchange exchange);

    protected abstract void logMessage();

    protected void updateSubjectValues(Exchange exchange, OperationId operationId) {

        this.subjectExchange = exchange;

        for (Map.Entry<String, String> entry : subjectDataValues.entrySet()) {

            var requestUpdateExchange = operationIdFactory.createExchange(operationId, subjectExchange);

            UpdateBusinessDataValueDTO subjectDataValueDTO = new UpdateBusinessDataValueDTO();
            subjectDataValueDTO.setReference(entry.getKey());
            subjectDataValueDTO.setValue(entry.getValue());
            requestUpdateExchange.getMessage().setBody(subjectDataValueDTO);

            Exchange responseUpdateExchange = efileConnector.send(requestUpdateExchange);

            if (responseUpdateExchange.isRouteStop()) {
                subjectExchange.setRouteStop(true);
                break;
            }
        }

        if (!subjectExchange.isRouteStop())
            logMessage();

    }

}
