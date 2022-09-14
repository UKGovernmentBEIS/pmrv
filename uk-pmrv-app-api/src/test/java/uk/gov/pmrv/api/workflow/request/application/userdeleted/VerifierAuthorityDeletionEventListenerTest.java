package uk.gov.pmrv.api.workflow.request.application.userdeleted;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.verifier.VerifierRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service.SystemMessageNotificationRequestService;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityDeletionEventListenerTest {

    @InjectMocks
    private VerifierAuthorityDeletionEventListener listener;

    @Mock
    private SystemMessageNotificationRequestService systemMessageNotificationRequestService;

    @Mock
    private VerifierRequestTaskAssignmentService verifierRequestTaskAssignmentService;


    @Test
    void onVerifierUserDeletedEvent() {
        final String userId = "user";
        VerifierAuthorityDeletionEvent event = VerifierAuthorityDeletionEvent.builder().userId(userId).build();

        listener.onVerifierUserDeletedEvent(event);

        verify(systemMessageNotificationRequestService, times(1)).completeOpenSystemMessageNotificationRequests(userId);
        verify(verifierRequestTaskAssignmentService, times(1)).assignTasksOfDeletedVerifierToVbSiteContactOrRelease(userId);
    }
}
