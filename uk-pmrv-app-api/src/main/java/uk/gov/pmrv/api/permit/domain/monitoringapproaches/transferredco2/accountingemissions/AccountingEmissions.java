package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#chemicallyBound != (#accountingEmissionsDetails != null)}", 
    message = "permit.transferredCO2MonitoringApproach.accountingEmissions.chemicallyBound")
public class AccountingEmissions {

    private boolean chemicallyBound;

    @Valid
    private AccountingEmissionsDetails accountingEmissionsDetails;

}
