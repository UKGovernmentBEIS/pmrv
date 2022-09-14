package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    private PermitType permitType;
    
    private Permit permit;

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();
}
