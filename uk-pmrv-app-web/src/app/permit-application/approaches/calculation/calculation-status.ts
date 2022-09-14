import { CalculationMonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

/** Returns the status of the whole monitoring approach task */
export function status(state: PermitApplicationState): TaskItemStatus {
  const tiers = (state.permit.monitoringApproaches?.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers;

  const tiersStatuses = tiers?.length > 0 ? tiers.map((_, index) => categoryTierStatus(state, index)) : ['not started'];

  return staticStatuses.every((status) => state.permitSectionsCompleted[status]?.[0]) &&
    tiersStatuses.every((status) => status === 'complete')
    ? 'complete'
    : tiersStatuses.some((status) => status === 'needs review')
    ? 'needs review'
    : staticStatuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
      tiersStatuses.some((status) => status === 'in progress' || status === 'complete')
    ? 'in progress'
    : 'not started';
}

/** Returns the status of source stream category applier tier with given index */
export function categoryTierStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  return isCategoryValid(state, index) && areActivityDataValid(state, index)
    ? categoryTierStatuses.every((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'complete'
      : categoryTierStatuses.some((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'in progress'
      : 'not started'
    : 'needs review';
}

/** Returns the status of the given source stream category applied tier subtask */
export function categoryTierSubtaskStatus(
  state: PermitApplicationState,
  key: StatusKey,
  index: number,
): TaskItemStatus {
  switch (key) {
    case 'CALCULATION_Category':
      return state.permitSectionsCompleted[key]?.[index]
        ? isCategoryValid(state, index)
          ? 'complete'
          : 'needs review'
        : 'not started';
    case 'CALCULATION_Emission_Factor':
      return categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierEmissionFactorExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? 'complete'
          : 'in progress'
        : 'not started';
    case 'CALCULATION_Calorific':
      return categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierCalorificValueExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? 'complete'
          : 'in progress'
        : 'not started';
    case 'CALCULATION_Oxidation_Factor':
      return categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierOxidationFactorExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? 'complete'
          : 'in progress'
        : 'not started';
    case 'CALCULATION_Biomass_Fraction':
      return categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierBiomassFractionExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? 'complete'
          : 'in progress'
        : 'not started';
    case 'CALCULATION_Carbon_Content':
      return categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierCarbonContentExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? 'complete'
          : 'in progress'
        : 'not started';
    case 'CALCULATION_Conversion_Factor':
      return categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'not started'
        ? 'cannot start yet'
        : isCategoryTierConversionFactorExist(state, index)
        ? state.permitSectionsCompleted[key]?.[index]
          ? 'complete'
          : 'in progress'
        : 'not started';
    case 'CALCULATION_Activity_Data':
      return !doActivityDataExist(state, index)
        ? !areActivityDataPrerequisitesMet(state, index)
          ? 'cannot start yet'
          : 'not started'
        : !areActivityDataValid(state, index)
        ? 'needs review'
        : state.permitSectionsCompleted[key]?.[index]
        ? 'complete'
        : 'in progress';
    default:
      return state.permitSectionsCompleted[key]?.[index] ? 'complete' : 'not started';
  }
}

function isCategoryTierEmissionFactorExist(state: PermitApplicationState, index: number): boolean {
  return !!(
    (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
      ?.sourceStreamCategoryAppliedTiers?.[index] as any
  )?.emissionFactor;
}

function isCategoryTierCalorificValueExist(state: PermitApplicationState, index: number): boolean {
  return !!(
    (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
      ?.sourceStreamCategoryAppliedTiers?.[index] as any
  )?.netCalorificValue;
}

function isCategoryTierOxidationFactorExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.oxidationFactor;
}

function isCategoryTierBiomassFractionExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.biomassFraction;
}
function isCategoryTierCarbonContentExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.carbonContent;
}

function isCategoryTierConversionFactorExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.conversionFactor;
}

export function areCategoryTierPrerequisitesMet(state: PermitApplicationState): boolean {
  return state.permitSectionsCompleted.sourceStreams?.[0] && state.permitSectionsCompleted.emissionSources?.[0];
}

/** Returns true if reference state is valid and all ids used in stream category exist */
function isCategoryValid(state: PermitApplicationState, index: number): boolean {
  return (
    !!state.permit.sourceStreams.find(
      (stream) =>
        stream.id ===
        (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
          .sourceStreamCategoryAppliedTiers[index]?.sourceStreamCategory?.sourceStream,
    ) &&
    (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.emissionSources.every((id) =>
      state.permit.emissionSources.map((source) => source.id).includes(id),
    )
  );
}

function areActivityDataPrerequisitesMet(state: PermitApplicationState, index: number): boolean {
  return (
    state.permitSectionsCompleted.measurementDevicesOrMethods?.[0] &&
    categoryTierSubtaskStatus(state, 'CALCULATION_Category', index) === 'complete'
  );
}

function doActivityDataExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index]?.activityData;
}

export function areActivityDataValid(state: PermitApplicationState, index: number): boolean {
  return (
    !doActivityDataExist(state, index) ||
    (state.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.activityData?.measurementDevicesOrMethods.every((id) =>
      state.permit.measurementDevicesOrMethods.map((source) => source.id).includes(id),
    )
  );
}

export function planStatus(state: PermitApplicationState): TaskItemStatus {
  const plan = (state.permit.monitoringApproaches?.CALCULATION as CalculationMonitoringApproach)?.samplingPlan;
  return state.permitSectionsCompleted.CALCULATION_Plan?.[0]
    ? 'complete'
    : plan?.exist !== undefined
    ? 'in progress'
    : 'not started';
}

export const categoryTierStatuses = [
  'CALCULATION_Category',
  'CALCULATION_Activity_Data',
  'CALCULATION_Calorific',
  'CALCULATION_Emission_Factor',
  'CALCULATION_Oxidation_Factor',
  'CALCULATION_Carbon_Content',
  'CALCULATION_Conversion_Factor',
  'CALCULATION_Biomass_Fraction',
] as const;

export const staticStatuses = ['CALCULATION_Description', 'CALCULATION_Plan'] as const;
export type statuses =
  | 'CALCULATION_Category_Tier'
  | typeof staticStatuses[number]
  | typeof categoryTierStatuses[number];
