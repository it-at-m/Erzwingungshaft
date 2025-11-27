package de.muenchen.eh.claim.efile.operation.subjectdata;

import de.muenchen.eakte.api.rest.model.UpdateBusinessDataValueDTO;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.LogServiceClaim;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

abstract class UpdateSubjectData {

    protected final LogServiceClaim logServiceClaim;
    private Map<String, String> subjectDataValues;
    private final ProducerTemplate efileConnector;
    private final OperationIdFactory operationIdFactory;
    protected final Exchange subjectExchange;

    public UpdateSubjectData(LogServiceClaim logServiceClaim, Exchange exchange, ProducerTemplate efileConnector, OperationIdFactory operationIdFactory) {

        this.logServiceClaim = logServiceClaim;
        this.efileConnector = efileConnector;
        this.operationIdFactory = operationIdFactory;
        this.subjectExchange = exchange;

    }

    public Exchange execute() {

        this.subjectDataValues = subjectDataValuesBuilder();
        updateSubjectValues();
        return subjectExchange;

    }

    protected abstract Map<String, String> subjectDataValuesBuilder();

    protected abstract void logMessage();

    protected void updateSubjectValues() {

        var requestUpdateExchange = operationIdFactory.createExchange(OperationId.UPDATE_SUBJECT_DATA, subjectExchange);

        for (Map.Entry<String, String> entry : subjectDataValues.entrySet()) {

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
