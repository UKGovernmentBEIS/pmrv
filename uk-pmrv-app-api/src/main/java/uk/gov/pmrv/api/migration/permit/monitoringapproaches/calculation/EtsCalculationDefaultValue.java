package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsCalculationDefaultValue {

    private final String etsAccountId;
    private final String tierId;
    private final String parameter;
    private final String referenceSource;
    private final String defaultValue;
}
