package uk.gov.pmrv.api.workflow.request.flow.permitvariation.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Service
public class PermitVariationReviewDeterminationDeemedWithdrawnAndDecisionsValidator implements PermitReviewDeterminationAndDecisionsValidator<PermitVariationApplicationReviewRequestTaskPayload> {

    @Override
    public boolean isValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return true;
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.DEEMED_WITHDRAWN;
    }

    @Override
	public RequestType getRequestType() {
		return RequestType.PERMIT_VARIATION;
	}

}