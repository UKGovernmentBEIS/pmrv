package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecision;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@SpELExpression(expression = "{(#notes?.length() gt 0)}",
	message = "permit.review.decision.notes.should.exist")
@SpELExpression(expression = "{((#type eq 'OPERATOR_AMENDS_NEEDED' && #changesRequired?.isBlank() eq false) "
	+ "|| (#type ne 'OPERATOR_AMENDS_NEEDED' && #changesRequired == null))}",
	message = "permit.review.decision.amend.missing.changes.required")
@SpELExpression(expression = "{(#type eq 'OPERATOR_AMENDS_NEEDED') || (#files == null)}",
	message = "permit.review.decision.not.amend.with.files")
public class PermitIssuanceReviewDecision extends PermitReviewDecision {

	@JsonUnwrapped
	@Valid
	private PermitReviewDecisionRequiredChange requiredChange;
}
