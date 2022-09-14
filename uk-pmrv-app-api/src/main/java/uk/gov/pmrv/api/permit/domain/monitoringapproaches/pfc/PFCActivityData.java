package uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#massBalanceApproachUsed || (#massBalanceApproachUsed == false && (#tier eq 'TIER_1' || #tier eq 'TIER_2'))}",
    message = "permit.pfcMonitoringApproach.sourceStreamCategoryAppliedTiers.activityData.invalidTier")
@SpELExpression(expression = "{((#tier eq 'TIER_1') || (#massBalanceApproachUsed && (#tier eq 'TIER_2' || #tier eq 'TIER_3'))) == (#isHighestRequiredTier != null)}",
    message = "permit.pfcMonitoringApproach.sourceStreamCategoryAppliedTiers.activityData.isHighestRequiredTier")
public class PFCActivityData {

    private boolean massBalanceApproachUsed;

    @NotNull
    private ActivityDataTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
}
