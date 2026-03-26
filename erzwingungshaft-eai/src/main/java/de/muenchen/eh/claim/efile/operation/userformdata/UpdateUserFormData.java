package de.muenchen.eh.claim.efile.operation.userformdata;

import de.muenchen.eakte.api.rest.model.UpdateUserFormsDataRequestDTO;
import de.muenchen.eakte.api.rest.model.UserFormsReferenz;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.LogServiceClaim;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

abstract class UpdateUserFormData {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    protected final LogServiceClaim logServiceClaim;
    private Map<String, String> userFormDataValues;
    private final OperationIdFactory operationIdFactory;
    protected Exchange subjectExchange;

    public UpdateUserFormData(LogServiceClaim logServiceClaim, OperationIdFactory operationIdFactory) {

        this.logServiceClaim = logServiceClaim;
        this.operationIdFactory = operationIdFactory;

    }

    public Exchange execute(Exchange exchange, OperationId operationId) {

        this.userFormDataValues = userFormValuesBuilder(exchange);
        updateUserFormValues(exchange, operationId);
        return subjectExchange;

    }

    protected abstract Map<String, String> userFormValuesBuilder(Exchange exchange);

    protected abstract void logMessage();

    protected void updateUserFormValues(Exchange exchange, OperationId operationId) {

        this.subjectExchange = exchange;

        var requestUpdateExchange = operationIdFactory.createExchange(operationId, subjectExchange);

        UpdateUserFormsDataRequestDTO userFormsDataRequestDTO = new UpdateUserFormsDataRequestDTO();

        for (Map.Entry<String, String> entry : userFormDataValues.entrySet()) {

            UserFormsReferenz userFormsReferenz = new UserFormsReferenz();
            userFormsReferenz.setLhmbai151700Ufreference(entry.getKey());
            userFormsReferenz.setLhmbai151700Ufvalue(List.of(entry.getValue()));

            userFormsDataRequestDTO.addUserformsdataItem(userFormsReferenz);
        }

        requestUpdateExchange.getMessage().setBody(userFormsDataRequestDTO);
        Exchange responseUpdateExchange = efileConnector.send(requestUpdateExchange);

        if (responseUpdateExchange.isRouteStop()) {
            subjectExchange.setRouteStop(true);
        }

        if (!subjectExchange.isRouteStop())
            logMessage();

    }

}
