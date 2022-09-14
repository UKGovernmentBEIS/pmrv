package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandler;

@Component
public class MakePaymentCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public MakePaymentCreatedHandler(final RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.MAKE_PAYMENT;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.PAYMENT_EXPIRATION_DATE;
    }
}
