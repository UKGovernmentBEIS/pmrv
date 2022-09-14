import { PFCActivityData } from 'pmrv-api';

export function isWizardComplete(activityData: PFCActivityData): boolean {
  return (
    !!activityData &&
    ((activityData.massBalanceApproachUsed && activityData.tier === 'TIER_4') ||
      (!activityData.massBalanceApproachUsed && activityData.tier === 'TIER_2') ||
      activityData.isHighestRequiredTier ||
      activityData?.noHighestRequiredTierJustification?.isCostUnreasonable ||
      activityData?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
  );
}
