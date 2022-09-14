package uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InherentCO2MonitoringApproach extends PermitMonitoringApproachSection {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;
}
