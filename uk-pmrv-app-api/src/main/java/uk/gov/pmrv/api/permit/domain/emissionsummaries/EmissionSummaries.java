package uk.gov.pmrv.api.permit.domain.emissionsummaries;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmissionSummaries implements PermitSection {

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<EmissionSummary> emissionSummaries = new ArrayList<>();
}
