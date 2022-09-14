package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitVariationSaveReviewGroupDecisionAbstractRequestTaskActionPayload extends RequestTaskActionPayload {

	@NotNull
    @Valid
    private PermitVariationReviewDecision decision;
	
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
    
}
