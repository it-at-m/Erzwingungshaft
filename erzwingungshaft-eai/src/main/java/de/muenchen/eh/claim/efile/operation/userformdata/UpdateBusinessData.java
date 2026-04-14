package de.muenchen.eh.claim.efile.operation.userformdata;

import de.muenchen.eakte.api.rest.model.UpdateBusinessDataValueDTO;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.LogServiceClaim;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

abstract public class UpdateBusinessData {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    protected final LogServiceClaim logServiceClaim;
    private Map<String, String> userFormDataValues;
    private final OperationIdFactory operationIdFactory;
    protected Exchange subjectExchange;

    public UpdateBusinessData(LogServiceClaim logServiceClaim, OperationIdFactory operationIdFactory) {

        this.logServiceClaim = logServiceClaim;
        this.operationIdFactory = operationIdFactory;

    }

    public Exchange execute(Exchange exchange, OperationId operationId) {

        this.userFormDataValues = userFormValuesBuilder(exchange);
        updateBusinessValues(exchange, operationId);
        return subjectExchange;

    }

    protected abstract Map<String, String> userFormValuesBuilder(Exchange exchange);

    protected abstract void logMessage();

    protected void updateBusinessValues(Exchange exchange, OperationId operationId) {

        this.subjectExchange = exchange;

        var requestUpdateExchange = operationIdFactory.createExchange(operationId, subjectExchange);

        UpdateBusinessDataValueDTO updateBusinessData = new UpdateBusinessDataValueDTO();

        for (Map.Entry<String, String> entry : userFormDataValues.entrySet()) {

            updateBusinessData.setReference(entry.getKey());
            updateBusinessData.setValue(entry.getValue());

            requestUpdateExchange.getMessage().setBody(updateBusinessData);
            Exchange responseUpdateExchange = efileConnector.send(requestUpdateExchange);

            if (responseUpdateExchange.isRouteStop()) {
                subjectExchange.setRouteStop(true);
                break;
            }

            if (!subjectExchange.isRouteStop())
                logMessage();

        }
    }

}
