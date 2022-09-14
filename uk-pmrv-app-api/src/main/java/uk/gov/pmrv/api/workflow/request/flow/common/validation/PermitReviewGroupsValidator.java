package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitPayloadGroupDecidable;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.utils.PermitReviewUtils;

@Service
public class PermitReviewGroupsValidator<T extends PermitReviewDecision> {
	
    public boolean containsDecisionForAllPermitGroups(final PermitPayloadGroupDecidable<T> taskPayload) {
		final Map<PermitReviewGroup, T> permitReviewGroupDecisions = taskPayload
				.getReviewGroupDecisions();
		
		final Set<PermitReviewGroup> permitReviewGroups = PermitReviewUtils.getPermitReviewGroups(taskPayload.getPermit());
		
		return permitReviewGroups.stream()
				.filter(g -> !permitReviewGroupDecisions.containsKey(g)).findAny()
				.isEmpty();
    }
    
    public boolean containsAmendNeededGroups(final PermitPayloadGroupDecidable<T> taskPayload) {
    	return taskPayload
				.getReviewGroupDecisions().values().stream()
                .anyMatch(dec -> dec.getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED));
                
    }
}
