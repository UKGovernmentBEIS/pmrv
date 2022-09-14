package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationSaveApplicationReviewRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private Permit permit;
    
    @NotNull
	private PermitVariationDetails permitVariationDetails;
    
	@NotNull
	private Boolean permitVariationDetailsCompleted;

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
    
    private Boolean permitVariationDetailsReviewCompleted;
    
}
