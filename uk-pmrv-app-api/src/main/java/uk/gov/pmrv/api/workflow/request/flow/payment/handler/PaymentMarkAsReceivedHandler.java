package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMarkAsReceivedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentOutcome;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentCompleteService;

@Component
@RequiredArgsConstructor
public class PaymentMarkAsReceivedHandler implements RequestTaskActionHandler<PaymentMarkAsReceivedRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PaymentCompleteService paymentCompleteService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PaymentMarkAsReceivedRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        paymentCompleteService.markAsReceived(requestTask.getRequest(), payload.getReceivedDate());

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.PAYMENT_REVIEW_OUTCOME, PaymentReviewOutcome.MARK_AS_RECEIVED,
                BpmnProcessConstants.PAYMENT_OUTCOME, PaymentOutcome.SUCCEEDED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PAYMENT_MARK_AS_RECEIVED);
    }
}
