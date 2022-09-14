package uk.gov.pmrv.api.migration.permit.annualemissionstarget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsAnnualEmissionsTarget {

    private String etsAccountId;

    private String monitoringYear;
    private String emissionsTarget;
}
