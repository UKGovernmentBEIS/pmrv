package uk.gov.pmrv.api.workflow.bpmn.handler.payment;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceDelegator;

@ExtendWith(MockitoExtension.class)
class DeterminePaymentAmountHandlerTest {

    @InjectMocks
    private DeterminePaymentAmountHandler determinePaymentAmountHandler;

    @Mock
    private PaymentDetermineAmountServiceDelegator paymentDetermineAmountServiceDelegator;

    @Mock
    private DelegateExecution delegateExecution;

    @Test
    void execute() {
        String requestId = "1";
        BigDecimal paymentAmount = BigDecimal.valueOf(100.25);

        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(paymentDetermineAmountServiceDelegator.determineAmount(requestId)).thenReturn(paymentAmount);

        determinePaymentAmountHandler.execute(delegateExecution);

        verify(delegateExecution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(paymentDetermineAmountServiceDelegator, times(1)).determineAmount(requestId);
        verify(delegateExecution, times(1)).setVariable(BpmnProcessConstants.PAYMENT_AMOUNT, paymentAmount);
    }
}