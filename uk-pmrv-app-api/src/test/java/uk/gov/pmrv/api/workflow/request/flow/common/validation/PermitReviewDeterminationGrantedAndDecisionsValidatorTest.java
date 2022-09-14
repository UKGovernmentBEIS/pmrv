package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

class PermitReviewDeterminationGrantedAndDecisionsValidatorTest {

	@Test
	void isValid_valid() {
		Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = Map.of(
				PermitReviewGroup.CALCULATION, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
				PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
				);
		
		PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.reviewGroupDecisions(reviewGroupDecisions)
				.build();
		
		assertThat(new PermitReviewDeterminationGrantedAndDecisionsValidator<PermitIssuanceReviewDecision>().isValid(taskPayload)).isTrue();
	}
	
	@Test
	void isValid_invalid() {
		Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = Map.of(
				PermitReviewGroup.CALCULATION, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
				PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.REJECTED).build()
				);
		
		PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.reviewGroupDecisions(reviewGroupDecisions)
				.build();
		
		assertThat(new PermitReviewDeterminationGrantedAndDecisionsValidator<PermitIssuanceReviewDecision>().isValid(taskPayload)).isFalse();
	}
}
