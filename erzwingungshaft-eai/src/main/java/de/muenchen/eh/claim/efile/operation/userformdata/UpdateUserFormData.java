package de.muenchen.eh.claim.efile.operation.userformdata;

import de.muenchen.eakte.api.rest.model.UpdateUserFormsDataRequestDTO;
import de.muenchen.eakte.api.rest.model.UserFormsReferenz;
import de.muenchen.eh.claim.efile.EfileRouteBuilder;
import de.muenchen.eh.claim.efile.operation.OperationId;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.log.LogServiceClaim;
import java.util.ArrayList;
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
        updateBusinessValues(exchange, operationId);
        return subjectExchange;

    }

    protected abstract Map<String, String> userFormValuesBuilder(Exchange exchange);

    protected abstract void logMessage();

    protected void updateBusinessValues(Exchange exchange, OperationId operationId) {

        this.subjectExchange = exchange;

        var requestUpdateExchange = operationIdFactory.createExchange(operationId, subjectExchange);

        UpdateUserFormsDataRequestDTO updateUserFormDataReferences = new UpdateUserFormsDataRequestDTO();
        List<UserFormsReferenz> updateUserFormData = new ArrayList<>();

        for (Map.Entry<String, String> entry : userFormDataValues.entrySet()) {

            UserFormsReferenz ufr = new UserFormsReferenz();
            ufr.setLhmbai151700Ufreference(entry.getKey());
            ufr.setLhmbai151700Ufvalue(List.of(entry.getValue()));

            updateUserFormData.add(ufr);
        }

        updateUserFormDataReferences.setUserformsdata(updateUserFormData);

        requestUpdateExchange.getMessage().setBody(updateUserFormDataReferences);
        Exchange responseUpdateExchange = efileConnector.send(requestUpdateExchange);

        if (responseUpdateExchange.isRouteStop()) {
            subjectExchange.setRouteStop(true);

        }

        if (!subjectExchange.isRouteStop())
            logMessage();
    }

}
