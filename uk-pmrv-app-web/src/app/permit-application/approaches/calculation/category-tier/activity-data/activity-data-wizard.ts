import { CalculationActivityData } from 'pmrv-api';

export function isWizardComplete(activityData: CalculationActivityData): boolean {
  return (
    !!activityData &&
    !!activityData.measurementDevicesOrMethods?.[0] &&
    !!activityData.uncertainty &&
    (activityData.tier === 'TIER_4' ||
      activityData.isHighestRequiredTier ||
      activityData?.noHighestRequiredTierJustification?.isCostUnreasonable ||
      activityData?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
  );
}
