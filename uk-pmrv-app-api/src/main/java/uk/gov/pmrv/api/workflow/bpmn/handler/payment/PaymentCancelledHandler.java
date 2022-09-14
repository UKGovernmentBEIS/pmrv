package uk.gov.pmrv.api.workflow.bpmn.handler.payment;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentCompleteRequestActionService;

@Component
@RequiredArgsConstructor
public class PaymentCancelledHandler implements JavaDelegate {

    private final PaymentCompleteRequestActionService paymentCompleteRequestActionService;

    @Override
    public void execute(DelegateExecution execution) {
        paymentCompleteRequestActionService.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
