import { CalculationMonitoringApproach, CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { StatusKey } from '../../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../../store/permit-application.state';

export type categoryAppliedTier = Omit<
  CalculationSourceStreamCategoryAppliedTier,
  'sourceStreamCategory' | 'activityData'
>;

export const statusKeyToSubtaskNameMapper: Record<StatusKey, keyof CalculationSourceStreamCategoryAppliedTier> = {
  CALCULATION_Calorific: 'netCalorificValue',
  CALCULATION_Emission_Factor: 'emissionFactor',
  CALCULATION_Oxidation_Factor: 'oxidationFactor',
  CALCULATION_Carbon_Content: 'carbonContent',
  CALCULATION_Conversion_Factor: 'conversionFactor',
  CALCULATION_Biomass_Fraction: 'biomassFraction',
};
export const statusKeyTosubtaskUrlParamMapper: Record<StatusKey, string> = {
  CALCULATION_Calorific: 'calorific-value',
  CALCULATION_Emission_Factor: 'emission-factor',
  CALCULATION_Oxidation_Factor: 'oxidation-factor',
  CALCULATION_Carbon_Content: 'carbon-content',
  CALCULATION_Conversion_Factor: 'conversion-factor',
  CALCULATION_Biomass_Fraction: 'biomass-fraction',
};

export function getSubtaskData(state: PermitApplicationState, index: number, statusKey: StatusKey) {
  const sourceStreamCategory = (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index] as categoryAppliedTier;

  const subtask = statusKeyToSubtaskNameMapper[statusKey];
  return sourceStreamCategory?.[subtask];
}
