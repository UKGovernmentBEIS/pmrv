package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecision;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{((#type ne 'ACCEPTED') == (#variationScheduleItems?.size() eq 0)) || (#type eq 'ACCEPTED')}",
    message = "permitvariation.review.decision.not.accepted.with.scheduled.items")
@SpELExpression(expression = "{"
    + "("
    + "(#type eq 'OPERATOR_AMENDS_NEEDED') "
    + "&& (#requiredChanges?.size() gt 0) "
    + "&& (#requiredChanges.?[#this.changesRequired?.isBlank()].size() eq 0)"
    + ") || (#type ne 'OPERATOR_AMENDS_NEEDED' && (#requiredChanges?.size() eq 0))"
    + "}",
    message = "permit.review.decision.amend.missing.changes.required")
public class PermitVariationReviewDecision extends PermitReviewDecision {

    @Builder.Default
    private List<@NotBlank @Size(max = 10000) String> variationScheduleItems = new ArrayList<>();

    @Builder.Default
    @JsonInclude(Include.NON_EMPTY)
    @Valid
    private List<PermitReviewDecisionRequiredChange> requiredChanges = new ArrayList<>();

}
