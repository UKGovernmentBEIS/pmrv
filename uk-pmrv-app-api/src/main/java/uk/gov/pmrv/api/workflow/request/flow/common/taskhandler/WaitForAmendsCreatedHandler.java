package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
public class WaitForAmendsCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public WaitForAmendsCreatedHandler(final RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.WAIT_FOR_AMENDS;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE;
    }
}
