package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation.PermitReviewNotifyOperatorValidator;

@Component
@RequiredArgsConstructor
public class PermitReviewNotifyOperatorActionHandler
    implements RequestTaskActionHandler<PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitReviewNotifyOperatorValidator validator;
    private final PermitIssuanceReviewService permitIssuanceReviewService;
    private final WorkflowService workflowService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        validator.validate(requestTask, payload, pmrvUser);

        final DecisionNotification permitDecisionNotification = payload.getPermitDecisionNotification();
        permitIssuanceReviewService.savePermitDecisionNotification(requestTask, permitDecisionNotification, pmrvUser);

        // complete task
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final DeterminationType determinationType = taskPayload.getDetermination().getType();
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                   BpmnProcessConstants.REVIEW_DETERMINATION, determinationType,
                   BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
