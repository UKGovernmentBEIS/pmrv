package uk.gov.pmrv.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier.VerifierRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service.SystemMessageNotificationRequestService;

@RequiredArgsConstructor
@Component(value = "workflowVerifierAuthorityDeletionEventListener")
public class VerifierAuthorityDeletionEventListener {

    private final VerifierRequestTaskAssignmentService verifierRequestTaskAssignmentService;
    private final SystemMessageNotificationRequestService systemMessageNotificationRequestService;

    @Order(2)
    @EventListener(VerifierAuthorityDeletionEvent.class)
    public void onVerifierUserDeletedEvent(VerifierAuthorityDeletionEvent event) {
        String userDeleted = event.getUserId();
        systemMessageNotificationRequestService.completeOpenSystemMessageNotificationRequests(userDeleted);
        verifierRequestTaskAssignmentService.assignTasksOfDeletedVerifierToVbSiteContactOrRelease(userDeleted);
    }

}
