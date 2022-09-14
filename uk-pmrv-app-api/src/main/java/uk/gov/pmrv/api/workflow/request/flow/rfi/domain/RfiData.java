package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfiData {

	private RfiQuestionPayload rfiQuestionPayload;
	private LocalDate rfiDeadline;
	private RfiResponsePayload rfiResponsePayload;
	
	@Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();
	
}
