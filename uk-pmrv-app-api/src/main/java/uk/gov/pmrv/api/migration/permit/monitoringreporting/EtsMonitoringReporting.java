package uk.gov.pmrv.api.migration.permit.monitoringreporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsMonitoringReporting {

    private String emitterId;

    private String jobTitle;

    private String mainDuties;

    private String organisationChart;
}
