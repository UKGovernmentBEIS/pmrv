package uk.gov.pmrv.api.workflow.request.flow.permitvariation.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationGrantedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationReviewDecision;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewDeterminationGrantedAndDecisionsValidator implements PermitReviewDeterminationAndDecisionsValidator<PermitVariationApplicationReviewRequestTaskPayload> {

	private final PermitReviewDeterminationGrantedAndDecisionsValidator<PermitVariationReviewDecision> permitReviewDeterminationGrantedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitVariationReviewDecision> permitReviewGroupsValidator;
    
    @Override
    public boolean isValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return containsDecisionForMandatoryGroups(taskPayload) &&
        		isGrantedDeterminationValid(taskPayload);
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.GRANTED;
    }
    
    @Override
	public RequestType getRequestType() {
		return RequestType.PERMIT_VARIATION;
	}
    
    private boolean containsDecisionForMandatoryGroups(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
    	 return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) && 
    			 taskPayload.getPermitVariationDetailsReviewDecision() != null;
    }
    
    private boolean isGrantedDeterminationValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
    	return permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload) && 
				taskPayload.getPermitVariationDetailsReviewDecision().getType() == ReviewDecisionType.ACCEPTED;
    }

}
