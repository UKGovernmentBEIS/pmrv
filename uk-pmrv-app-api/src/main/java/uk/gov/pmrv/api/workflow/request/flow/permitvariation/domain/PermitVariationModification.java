package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(T(uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType).valueOf(#type).otherSummaryApplies) == (#otherSummary != null)}", 
	message = "permitvariation.modification.typeOtherSummary")
public class PermitVariationModification {
	
	@NotNull
	private PermitVariationModificationType type;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String otherSummary;
	
}
