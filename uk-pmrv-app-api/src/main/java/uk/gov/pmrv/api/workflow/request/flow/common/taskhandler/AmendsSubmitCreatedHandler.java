package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
public class AmendsSubmitCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public AmendsSubmitCreatedHandler(final RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.APPLICATION_AMENDS_SUBMIT;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE;
    }
}
