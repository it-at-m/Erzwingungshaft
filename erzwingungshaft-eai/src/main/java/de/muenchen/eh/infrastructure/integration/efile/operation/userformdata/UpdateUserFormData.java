package de.muenchen.eh.infrastructure.integration.efile.operation.userformdata;

import de.muenchen.eakte.api.rest.model.UpdateUserFormsDataRequestDTO;
import de.muenchen.eakte.api.rest.model.UserFormsReferenz;
import de.muenchen.eh.infrastructure.integration.efile.EfileRouteBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationIdFactory;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

abstract class UpdateUserFormData {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    /*
     * Github coderabbit drew attention to this problem:
     * UpdateFileUserFormData is a Spring @Component (singleton), but UpdateUserFormData stores
     * per-invocation state in instance fields: userFormDataValues (set in execute(),
     * read in updateBusinessValues()) and subjectExchange (set in updateBusinessValues(), returned from
     * execute() and read by the subclass's logMessage()).
     * The subclass also has subjectProperties set in userFormValuesBuilder() and read in logMessage().
     * If two exchanges are processed concurrently, one invocation can overwrite another's field values
     * before they are read,
     * causing wrong user-form data to be sent or wrong log messages to be written.
     *
     * Currently, no UserFormData updates are being performed via the EAI configuration. From a
     * functional standpoint,
     * this is not intended to be an EAI task in the future either.
     * Consequently, investigating the problem is not a priority for the time being.
     *
     */

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
