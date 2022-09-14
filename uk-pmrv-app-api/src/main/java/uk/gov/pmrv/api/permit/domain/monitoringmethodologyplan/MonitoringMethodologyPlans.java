package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#plans?.size() gt 0)}", message = "permit.monitoringmethodologyplans.exist")
public class MonitoringMethodologyPlans implements PermitSection {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> plans = new HashSet<>();
}