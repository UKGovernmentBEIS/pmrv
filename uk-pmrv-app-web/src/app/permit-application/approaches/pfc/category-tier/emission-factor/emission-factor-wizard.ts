import { PFCEmissionFactor } from 'pmrv-api';

export function isWizardComplete(emissionFactor: PFCEmissionFactor): boolean {
  return (
    !!emissionFactor &&
    (emissionFactor.tier === 'TIER_2' ||
      emissionFactor.isHighestRequiredTier ||
      emissionFactor?.noHighestRequiredTierJustification?.isCostUnreasonable ||
      emissionFactor?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
  );
}
