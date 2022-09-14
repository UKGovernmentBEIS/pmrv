package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType.PAYMENT_MARK_AS_PAID;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentOutcome;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentCompleteService;

@ExtendWith(MockitoExtension.class)
class PaymentMarkAsPaidHandlerTest {

    @InjectMocks
    private PaymentMarkAsPaidHandler paymentMarkAsPaidHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PaymentCompleteService paymentCompleteService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .processTaskId("process-123")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        paymentMarkAsPaidHandler.process(requestTaskId, PAYMENT_MARK_AS_PAID, pmrvUser, new RequestTaskActionEmptyPayload());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(paymentCompleteService, times(1)).markAsPaid(requestTask, pmrvUser);
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId(), Map.of(BpmnProcessConstants.PAYMENT_OUTCOME, PaymentOutcome.MARK_AS_PAID));
    }

    @Test
    void getTypes() {
        assertThat(paymentMarkAsPaidHandler.getTypes()).isEqualTo(List.of(PAYMENT_MARK_AS_PAID));
    }
}