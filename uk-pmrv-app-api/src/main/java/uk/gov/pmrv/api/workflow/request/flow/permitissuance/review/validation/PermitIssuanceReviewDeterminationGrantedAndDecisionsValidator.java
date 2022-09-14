package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationGrantedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

@Service
@RequiredArgsConstructor
public class PermitIssuanceReviewDeterminationGrantedAndDecisionsValidator implements PermitReviewDeterminationAndDecisionsValidator<PermitIssuanceApplicationReviewRequestTaskPayload> {

	private final PermitReviewDeterminationGrantedAndDecisionsValidator<PermitIssuanceReviewDecision> permitReviewDeterminationGrantedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitIssuanceReviewDecision> permitReviewGroupsValidator;

    @Override
    public boolean isValid(PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
    	return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) &&
    			permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)
        		;
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.GRANTED;
    }
    
    @Override
	public RequestType getRequestType() {
		return RequestType.PERMIT_ISSUANCE;
	}

}
