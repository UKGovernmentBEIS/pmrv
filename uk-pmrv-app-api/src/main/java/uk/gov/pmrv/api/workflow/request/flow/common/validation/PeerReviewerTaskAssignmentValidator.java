package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@Service
@RequiredArgsConstructor
public class PeerReviewerTaskAssignmentValidator {

    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    public void validate(RequestTaskType requestTaskType, String selectedPeerReviewer, PmrvUser pmrvUser) {
        if (!requestTaskAssignmentValidationService
            .hasUserPermissionsToBeAssignedToTaskType(requestTaskType, selectedPeerReviewer, pmrvUser)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }
}
