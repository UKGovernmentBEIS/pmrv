package uk.gov.pmrv.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator.RegulatorRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service.SystemMessageNotificationRequestService;

@RequiredArgsConstructor
@Component(value = "workflowRegulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final SystemMessageNotificationRequestService systemMessageNotificationRequestService;
    private final RegulatorRequestTaskAssignmentService regulatorRequestTaskAssignmentService;

    @Order(2)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorUserDeletedEvent(RegulatorAuthorityDeletionEvent event) {
        String userDeleted = event.getUserId();
        systemMessageNotificationRequestService.completeOpenSystemMessageNotificationRequests(userDeleted);
        regulatorRequestTaskAssignmentService.assignTasksOfDeletedRegulatorToCaSiteContactOrRelease(userDeleted);
    }
}
