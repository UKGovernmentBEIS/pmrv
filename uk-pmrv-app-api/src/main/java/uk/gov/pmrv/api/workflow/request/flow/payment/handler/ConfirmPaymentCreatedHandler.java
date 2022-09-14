package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicNonExpirableUserTaskCreatedHandler;

@Component
public class ConfirmPaymentCreatedHandler extends WorkflowDynamicNonExpirableUserTaskCreatedHandler {

    public ConfirmPaymentCreatedHandler(RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.CONFIRM_PAYMENT;
    }

}
