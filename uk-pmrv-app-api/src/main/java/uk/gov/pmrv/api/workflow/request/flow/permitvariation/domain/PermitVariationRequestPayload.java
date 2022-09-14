package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermitVariationRequestPayload extends RequestPayload implements RequestPayloadRfiable, RequestPayloadRdeable {
	
	private PermitType permitType;

	private Permit permit;
	
	private PermitVariationDetails permitVariationDetails;
	
	private Boolean permitVariationDetailsCompleted;

	@Builder.Default
	private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

	@Builder.Default
	private Map<UUID, String> permitAttachments = new HashMap<>();
	
	@Builder.Default
	private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
	
	private Boolean permitVariationDetailsReviewCompleted;
	
	@JsonUnwrapped
    private RfiData rfiData;
	
	@JsonUnwrapped
    private RdeData rdeData;
}
