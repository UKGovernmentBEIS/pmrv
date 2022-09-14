package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;

@Service
public class PermitIssuanceReviewDeterminationDeemedWithdrawnAndDecisionsValidator implements PermitReviewDeterminationAndDecisionsValidator<PermitIssuanceApplicationReviewRequestTaskPayload> {

    @Override
    public boolean isValid(PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
        return true;
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.DEEMED_WITHDRAWN;
    }

    @Override
	public RequestType getRequestType() {
		return RequestType.PERMIT_ISSUANCE;
	}

}