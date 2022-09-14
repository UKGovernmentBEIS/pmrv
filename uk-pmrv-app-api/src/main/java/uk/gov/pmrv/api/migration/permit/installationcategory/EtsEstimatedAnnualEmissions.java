package uk.gov.pmrv.api.migration.permit.installationcategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEstimatedAnnualEmissions {

    private String emitterId;

    private String estimatedAnnualEmission;
}
