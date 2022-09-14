package uk.gov.pmrv.api.permit.domain.emissionsummaries;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
@SpELExpression(expression = "{#excludedRegulatedActivity != (#regulatedActivity != null)}", message = "permit.emissionSummary.excludedRegulatedActivity")
public class EmissionSummary implements PermitSection {

    @NotBlank
    private String sourceStream;

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionSources = new LinkedHashSet<>();

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionPoints = new LinkedHashSet<>();
    
    private boolean excludedRegulatedActivity;

    @Size(max=1000)
    private String regulatedActivity;

}
