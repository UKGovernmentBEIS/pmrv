package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationValidatorTest {

    @InjectMocks
    private PermitRevocationValidator permitRevocationValidator;

    @Mock
    private Validator validator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    

    @Test
    void validateUsers() {

        final String userId = "userId";
        final PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        final PermitRevocation permitRevocation = PermitRevocation.builder()
            .reason("reason")
            .activitiesStopped(false)
            .feeCharged(true)
            .build();
        final PermitRevocationApplicationSubmitRequestTaskPayload requestTaskPayload =
            PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .permitRevocation(permitRevocation)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT)
            .payload(requestTaskPayload)
            .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("user1", "user2"))
            .signatory("signatory")
            .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)).thenReturn(
            true);

        permitRevocationValidator.validateNotifyUsers(requestTask, decisionNotification, pmrvUser);

        verify(decisionNotificationUsersValidator, times(1))
            .areUsersValid(requestTask, decisionNotification, pmrvUser);
    }

    @Test
    void validatePeerReviewer() {

        final String peerReviewer = "peerReviewer";
        final String userId = "userId";
        final PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        permitRevocationValidator.validatePeerReviewer(peerReviewer, pmrvUser);

        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW, peerReviewer, pmrvUser);
    }
}