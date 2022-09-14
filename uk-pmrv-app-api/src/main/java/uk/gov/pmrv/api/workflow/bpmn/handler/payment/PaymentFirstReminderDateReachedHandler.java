package uk.gov.pmrv.api.workflow.bpmn.handler.payment;

import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentSendReminderNotificationService;

@Service
@RequiredArgsConstructor
public class PaymentFirstReminderDateReachedHandler implements JavaDelegate {

    private final PaymentSendReminderNotificationService paymentSendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) delegateExecution.getVariable(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE);
        
        paymentSendReminderNotificationService.sendFirstReminderNotification(requestId, expirationDate);
    }
}
