package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

@Component
@RequiredArgsConstructor
public class PermitNotificationFollowUpReviewNotifyOperatorActionHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitNotificationValidatorService permitNotificationValidatorService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitNotificationFollowUpReviewDecision reviewDecision = taskPayload.getReviewDecision();

        final DecisionNotification decisionNotification = actionPayload.getDecisionNotification();

        // validate
        permitNotificationValidatorService.validateNotificationFollowUpReviewDecision(reviewDecision);
        permitNotificationValidatorService.validateNotifyUsers(requestTask, decisionNotification, pmrvUser);

        // update request payload
        final Request request = requestTask.getRequest();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        requestPayload.setFollowUpResponseSubmissionDate(taskPayload.getSubmissionDate());
        requestPayload.setFollowUpReviewDecision(reviewDecision);
        requestPayload.setFollowUpReviewDecisionNotification(decisionNotification);

        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
