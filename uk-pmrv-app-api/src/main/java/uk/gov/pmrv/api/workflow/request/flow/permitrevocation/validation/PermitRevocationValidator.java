package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@Service
@Validated
@RequiredArgsConstructor
public class PermitRevocationValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    public void validateSubmitRequestTaskPayload(@NotNull @Valid @SuppressWarnings("unused")
                                                 final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload) {
        // default validation
    }

    public void validateWaitForAppealRequestTaskPayload(@NotNull @Valid @SuppressWarnings("unused")
                                                 final PermitRevocationWaitForAppealRequestTaskPayload taskPayload) {
        // default validation
    }


    public void validateNotifyUsers(final RequestTask requestTask,
                                    final DecisionNotification decisionNotification,
                                    final PmrvUser pmrvUser) {
        
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validatePeerReviewer(final String peerReviewer,
                                     final PmrvUser pmrvUser) {
        
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW,
                                                     peerReviewer, 
                                                     pmrvUser);
    }
}
