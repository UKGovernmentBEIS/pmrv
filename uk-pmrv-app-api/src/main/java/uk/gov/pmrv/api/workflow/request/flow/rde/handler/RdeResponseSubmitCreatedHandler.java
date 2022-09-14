package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandler;

@Component
public class RdeResponseSubmitCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public RdeResponseSubmitCreatedHandler(RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.RDE_RESPONSE_SUBMIT;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.RDE_EXPIRATION_DATE;
    }
}
