import { AccountingEmissions } from 'pmrv-api';

export function isWizardComplete(accountingEmissions: AccountingEmissions): boolean {
  return !!accountingEmissions && (accountingEmissions.chemicallyBound ?? true) === true
    ? accountingEmissions.chemicallyBound != undefined
    : !!accountingEmissions?.accountingEmissionsDetails &&
        (accountingEmissions.accountingEmissionsDetails.isHighestRequiredTier ||
          accountingEmissions.accountingEmissionsDetails.tier === 'TIER_4' ||
          accountingEmissions.accountingEmissionsDetails.tier === 'NO_TIER' ||
          accountingEmissions.accountingEmissionsDetails?.noHighestRequiredTierJustification?.isCostUnreasonable ||
          accountingEmissions.accountingEmissionsDetails?.noHighestRequiredTierJustification?.isTechnicallyInfeasible);
}
