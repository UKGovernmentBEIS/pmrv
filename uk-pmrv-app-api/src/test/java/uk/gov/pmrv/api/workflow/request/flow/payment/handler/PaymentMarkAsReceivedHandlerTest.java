package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType.PAYMENT_MARK_AS_RECEIVED;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMarkAsReceivedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentOutcome;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentCompleteService;

@ExtendWith(MockitoExtension.class)
class PaymentMarkAsReceivedHandlerTest {

    @InjectMocks
    private PaymentMarkAsReceivedHandler paymentMarkAsReceivedHandler;

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
        LocalDate receivedDate = LocalDate.now().minusDays(3);
        PaymentMarkAsReceivedRequestTaskActionPayload receivedRequestTaskActionPayload = PaymentMarkAsReceivedRequestTaskActionPayload
            .builder()
            .receivedDate(receivedDate)
            .build();
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .processTaskId("process-123")
            .request(request)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        paymentMarkAsReceivedHandler
            .process(requestTaskId, PAYMENT_MARK_AS_RECEIVED, pmrvUser, receivedRequestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(paymentCompleteService, times(1)).markAsReceived(request, receivedDate);
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.PAYMENT_REVIEW_OUTCOME, PaymentReviewOutcome.MARK_AS_RECEIVED,
                        BpmnProcessConstants.PAYMENT_OUTCOME, PaymentOutcome.SUCCEEDED));
    }

    @Test
    void getTypes() {
        assertThat(paymentMarkAsReceivedHandler.getTypes()).isEqualTo(List.of(PAYMENT_MARK_AS_RECEIVED));
    }
}