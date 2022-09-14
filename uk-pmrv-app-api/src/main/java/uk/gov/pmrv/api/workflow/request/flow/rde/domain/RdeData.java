package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RdeData {
	
	// the data for the rde submittance
    private RdePayload rdePayload;

    // the data for the rde response by the operator
    private RdeDecisionPayload rdeDecisionPayload;

    // the data for the rde response when it is forced by the regulator
    private RdeForceDecisionPayload rdeForceDecisionPayload;

    @Builder.Default
    private Map<UUID, String> rdeAttachments = new HashMap<>();
    
    private LocalDate currentDueDate;

}
