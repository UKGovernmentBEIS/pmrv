package uk.gov.pmrv.api.reporting.domain.pollutantregistercodes;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{#exist == (#activities?.size() gt 0)}", message = "aer.pollutantRegisterActivities.exist")
public class PollutantRegisterActivities {

    private boolean exist;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<PollutantRegisterActivity> activities = new HashSet<>();
}
