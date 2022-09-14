package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationValidatorServiceTest {

    @InjectMocks
    private PermitNotificationValidatorService validator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validateNotifyUsers() {
        final RequestTask requestTask = RequestTask.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)).thenReturn(true);

        // Invoke
        validator.validateNotifyUsers(requestTask, decisionNotification, pmrvUser);

        // Verify
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, pmrvUser);
    }

    @Test
    void validateNotifyUsers_not_valid() {
        final RequestTask requestTask = RequestTask.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validateNotifyUsers(requestTask, decisionNotification, pmrvUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, pmrvUser);
    }

    @Test
    void validatePeerReviewer() {
        String peerReviewer = "selectedPeerReviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        // Invoke
        validator.validatePeerReviewer(peerReviewer, pmrvUser);

        // Verify
        verify(peerReviewerTaskAssignmentValidator, times(1))
                .validate(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW, peerReviewer, pmrvUser);
    }

    @Test
    void validateNotificationFollowUpReviewDecision() {

        final PermitNotificationFollowUpReviewDecision reviewDecision = 
            PermitNotificationFollowUpReviewDecision.builder()
                .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                .build();

        final BusinessException businessException = assertThrows(BusinessException.class,
            () -> validator.validateNotificationFollowUpReviewDecision(reviewDecision));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
    }
}
