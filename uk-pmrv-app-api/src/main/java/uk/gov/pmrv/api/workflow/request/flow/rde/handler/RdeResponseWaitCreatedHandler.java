package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandler;

@Component
public class RdeResponseWaitCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public RdeResponseWaitCreatedHandler(RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.RDE_EXPIRATION_DATE;
    }
}
