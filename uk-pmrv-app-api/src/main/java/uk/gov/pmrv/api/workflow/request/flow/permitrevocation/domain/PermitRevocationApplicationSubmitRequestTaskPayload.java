package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain;

import java.math.BigDecimal;
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
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PermitRevocationApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    @Valid
    private PermitRevocation permitRevocation;

    @NotNull
    private BigDecimal feeAmount;
    
    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
