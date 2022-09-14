package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@ExtendWith(MockitoExtension.class)
class PeerReviewerTaskAssignmentValidatorTest {

    @InjectMocks
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Test
    void validate() {
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        String peerReviewer = "peerReviewer";
        RequestTaskType requestTaskType = RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW;

        when(requestTaskAssignmentValidationService.
            hasUserPermissionsToBeAssignedToTaskType(requestTaskType, peerReviewer, pmrvUser))
            .thenReturn(true);

        peerReviewerTaskAssignmentValidator.validate(requestTaskType, peerReviewer, pmrvUser);
    }

    @Test
    void validate_assignment_not_allowed() {
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        String peerReviewer = "peerReviewer";
        RequestTaskType requestTaskType = RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW;

        when(requestTaskAssignmentValidationService.
            hasUserPermissionsToBeAssignedToTaskType(requestTaskType, peerReviewer, pmrvUser))
            .thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> peerReviewerTaskAssignmentValidator.validate(requestTaskType, peerReviewer, pmrvUser));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
    }
}