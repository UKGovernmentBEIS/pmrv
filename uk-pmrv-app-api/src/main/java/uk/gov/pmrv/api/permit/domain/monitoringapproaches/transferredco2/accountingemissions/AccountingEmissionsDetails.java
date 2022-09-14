package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasuredEmissions;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'NO_TIER') == (#noTierJustification != null)}", 
    message = "permit.transferredCO2MonitoringApproach.accountingEmissions.noTierJustification")
@SpELExpression(expression = "{(#tier eq 'TIER_1' or #tier eq 'TIER_2' or #tier eq 'TIER_3') == (#isHighestRequiredTier != null)}", 
    message = "permit.transferredCO2MonitoringApproach.accountingEmissions.isHighestRequiredTier_tier_1_2_3")
@SpELExpression(expression = "{(#tier eq 'TIER_4' or #tier eq 'NO_TIER') == (#isHighestRequiredTier == null)}", 
    message = "permit.transferredCO2MonitoringApproach.accountingEmissions.isHighestRequiredTier_tier_4_noTier")
public class AccountingEmissionsDetails extends MeasuredEmissions {

    @NotNull
    private AccountingEmissionsTier tier;

    @Size(max = 10000)
    private String noTierJustification;
}
