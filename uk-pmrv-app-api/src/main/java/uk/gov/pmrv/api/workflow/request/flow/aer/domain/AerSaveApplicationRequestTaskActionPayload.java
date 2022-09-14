package uk.gov.pmrv.api.workflow.request.flow.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AerSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {
    
    private Aer aer;

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();
}
