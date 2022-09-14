package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandler;

@Component
public class RfiResponseSubmitCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public RfiResponseSubmitCreatedHandler(RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.RFI_RESPONSE_SUBMIT;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.RFI_EXPIRATION_DATE;
    }
}
