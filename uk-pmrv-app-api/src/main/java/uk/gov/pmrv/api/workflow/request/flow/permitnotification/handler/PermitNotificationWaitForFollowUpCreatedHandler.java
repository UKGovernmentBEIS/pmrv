package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandler;

@Component
public class PermitNotificationWaitForFollowUpCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public PermitNotificationWaitForFollowUpCreatedHandler(RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.WAIT_FOR_FOLLOW_UP;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE;
    }
}
