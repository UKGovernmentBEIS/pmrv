package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper.PermitReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitReviewRequestPeerReviewValidator {

    private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    private final PermitGrantedValidatorService permitGrantedValidatorService;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private static final PermitReviewMapper permitReviewMapper = Mappers.getMapper(PermitReviewMapper.class);

    public void validate(RequestTask requestTask,
                         PeerReviewRequestTaskActionPayload payload,
                         PmrvUser pmrvUser) {

        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(), pmrvUser);

        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        validateDeterminationAndDecisions(taskPayload.getDetermination(), taskPayload, requestTask.getRequest().getType());
        validatePermit(taskPayload.getDetermination().getType(), taskPayload);
    }

    private void validateDeterminationAndDecisions(PermitIssuanceDeterminateable reviewDetermination,
                                            PermitIssuanceApplicationReviewRequestTaskPayload taskPayload,
                                            RequestType requestType) {
    	permitReviewDeterminationValidatorService.validateDeterminationObject(reviewDetermination);
        if(!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(reviewDetermination, taskPayload, requestType)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validatePermit(DeterminationType reviewDeterminationType, PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
        PermitContainer permitContainer = permitReviewMapper.toPermitContainer(taskPayload);

        if(DeterminationType.GRANTED.equals(reviewDeterminationType)) {
            permitGrantedValidatorService.validatePermit(permitContainer);
        } 

    }
}
