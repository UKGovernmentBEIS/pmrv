package uk.gov.pmrv.api.permit.domain.monitoringreporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringRole {

    @NotBlank
    @Size(max = 10000)
    private String jobTitle;

    @NotBlank
    @Size(max = 10000)
    private String mainDuties;
}
