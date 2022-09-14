package uk.gov.pmrv.api.permit.domain.monitoringreporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
public class MonitoringReporting implements PermitSection {

    @NotEmpty
    @Valid
    @Builder.Default
    private List<MonitoringRole> monitoringRoles = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> organisationCharts;
}
