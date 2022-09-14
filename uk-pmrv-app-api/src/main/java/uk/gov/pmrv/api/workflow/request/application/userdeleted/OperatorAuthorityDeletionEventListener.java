package uk.gov.pmrv.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service.SystemMessageNotificationRequestService;

@RequiredArgsConstructor
@Component(value =  "workflowOperatorAuthorityDeletionEventListener")
public class OperatorAuthorityDeletionEventListener {

    private final SystemMessageNotificationRequestService systemMessageNotificationRequestService;
    private final OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Order(2)
    @EventListener(OperatorAuthorityDeletionEvent.class)
    public void onOperatorUserDeletionEvent(OperatorAuthorityDeletionEvent deletionEvent) throws BusinessCheckedException {
        String deletedUserId = deletionEvent.getUserId();
        Long accountId = deletionEvent.getAccountId();
        systemMessageNotificationRequestService.completeOpenSystemMessageNotificationRequests(deletedUserId, accountId);
        operatorRequestTaskAssignmentService.assignUserTasksToAccountPrimaryContactOrRelease(deletedUserId, accountId);
    }
}
