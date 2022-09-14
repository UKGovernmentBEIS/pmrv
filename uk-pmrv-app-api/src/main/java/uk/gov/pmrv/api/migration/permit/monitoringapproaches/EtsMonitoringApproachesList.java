package uk.gov.pmrv.api.migration.permit.monitoringapproaches;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsMonitoringApproachesList {
    private String etsAccountId;
    private boolean calculation;
    private boolean measurement;
    private boolean fallback;
    private boolean n20;
    private boolean pfc;
    private boolean co2;
}
