package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit;

import java.util.Map;

import uk.gov.pmrv.api.permit.domain.Permit;

public interface PermitPayloadGroupDecidable<T extends PermitReviewDecision> {

	Permit getPermit();
	
	Map<PermitReviewGroup, T> getReviewGroupDecisions();
	
}
