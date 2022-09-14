package uk.gov.pmrv.api.migration.permit.monitoringapproaches.inherentco2;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsInherentCO2MonitoringApproachDescription {

    private String etsAccountId;
    private String approachDescription;
}
