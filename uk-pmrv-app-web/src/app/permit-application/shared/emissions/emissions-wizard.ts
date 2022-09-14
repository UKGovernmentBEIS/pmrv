import { MeasMeasuredEmissions, N2OMeasuredEmissions } from 'pmrv-api';

import { SubTaskKey } from '../types/permit-task.type';

export function isWizardComplete(
  taskKey: SubTaskKey,
  measuredEmissions: N2OMeasuredEmissions | MeasMeasuredEmissions,
): boolean {
  if (taskKey === 'MEASUREMENT') {
    return (
      !!measuredEmissions &&
      (measuredEmissions.isHighestRequiredTier ||
        measuredEmissions.tier === 'TIER_4' ||
        measuredEmissions.tier === 'NO_TIER' ||
        measuredEmissions?.noHighestRequiredTierJustification?.isCostUnreasonable ||
        measuredEmissions?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
    );
  } else {
    return (
      !!measuredEmissions &&
      (measuredEmissions.isHighestRequiredTier ||
        measuredEmissions.tier === 'TIER_3' ||
        measuredEmissions?.noHighestRequiredTierJustification?.isCostUnreasonable ||
        measuredEmissions?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
    );
  }
}
