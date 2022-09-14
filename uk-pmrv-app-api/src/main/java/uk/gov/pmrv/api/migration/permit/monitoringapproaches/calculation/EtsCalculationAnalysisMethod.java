package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsCalculationAnalysisMethod {

    private final String etsAccountId;
    private final String tierId;
    private final String parameter;
    private final String analysisMethod;
    private final boolean otherFrequency;
    private final String frequency;
    private final boolean frequencyMeetsMinRequirements;
    private final String reducedFrequencyReason;
    private final String laboratoryName;
    private final boolean laboratoryAccredited;
}
