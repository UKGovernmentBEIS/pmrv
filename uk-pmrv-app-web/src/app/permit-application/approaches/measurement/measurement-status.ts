import { MeasMonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

export function MEASUREMENTStatus(state: PermitApplicationState): TaskItemStatus {
  const tiers = (state.permit.monitoringApproaches?.MEASUREMENT as MeasMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers;

  const tiersStatuses =
    tiers?.length > 0 ? tiers.map((_, index) => MEASUREMENTCategoryTierStatus(state, index)) : ['not started'];

  return measurementStaticStatuses.every((status) => state.permitSectionsCompleted[status]?.[0]) &&
    tiersStatuses.every((status) => status === 'complete')
    ? 'complete'
    : tiersStatuses.some((status) => status === 'needs review')
    ? 'needs review'
    : measurementStaticStatuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
      tiersStatuses.some((status) => status === 'in progress' || status === 'complete')
    ? 'in progress'
    : 'not started';
}

/** Returns the status of source stream category applier tier */
export function MEASUREMENTCategoryTierStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  return isMeasurementCategoryValid(state, index) &&
    MEASUREMENTCategoryTierSubtaskStatus(state, 'MEASUREMENT_Measured_Emissions', index) !== 'needs review'
    ? measurementCategoryTierStatuses.every((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'complete'
      : measurementCategoryTierStatuses.some((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'in progress'
      : 'not started'
    : 'needs review';
}

/** Returns the status of source stream category applied tier subtask */
export function MEASUREMENTCategoryTierSubtaskStatus(
  state: PermitApplicationState,
  key: StatusKey,
  index: number,
): TaskItemStatus {
  switch (key) {
    case 'MEASUREMENT_Category':
      return state.permitSectionsCompleted[key]?.[index]
        ? isMeasurementCategoryValid(state, index)
          ? 'complete'
          : 'needs review'
        : 'not started';
    case 'MEASUREMENT_Measured_Emissions': {
      return !doMeasMeasuredEmissionsExist(state, index)
        ? !areMeasMeasuredEmissionsPrerequisitesMet(state, index)
          ? 'cannot start yet'
          : 'not started'
        : !areMeasMeasuredEmissionsDevicesValid(state, index)
        ? 'needs review'
        : !state.permitSectionsCompleted[key]?.[index]
        ? 'in progress'
        : 'complete';
    }
    case 'MEASUREMENT_Applied_Standard':
      return state.permitSectionsCompleted[key]?.[index]
        ? 'complete'
        : MEASUREMENTCategoryTierSubtaskStatus(state, 'MEASUREMENT_Category', index) === 'complete'
        ? 'not started'
        : 'cannot start yet';
    default:
      return state.permitSectionsCompleted[key]?.[index] ? 'complete' : 'not started';
  }
}

export function areCategoryTierPrerequisitesMet(state: PermitApplicationState): boolean {
  return (
    state.permitSectionsCompleted.sourceStreams?.[0] &&
    state.permitSectionsCompleted.emissionPoints?.[0] &&
    state.permitSectionsCompleted.emissionSources?.[0]
  );
}

function doMeasMeasuredEmissionsExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach).sourceStreamCategoryAppliedTiers?.[
    index
  ]?.measuredEmissions;
}

export function areMeasMeasuredEmissionsPrerequisitesMet(state: PermitApplicationState, index: number): boolean {
  return (
    (!!(state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach).sourceStreamCategoryAppliedTiers?.[
      index
    ]?.measuredEmissions &&
      !!state.permit?.measurementDevicesOrMethods) ||
    (!!(state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach).sourceStreamCategoryAppliedTiers?.[
      index
    ]?.sourceStreamCategory &&
      state.permitSectionsCompleted.measurementDevicesOrMethods?.[0])
  );
}

export function areMeasMeasuredEmissionsDevicesValid(state: PermitApplicationState, index: number): boolean {
  const measuredEmissions = (state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach)
    .sourceStreamCategoryAppliedTiers?.[index]?.measuredEmissions;

  return measuredEmissions?.measurementDevicesOrMethods.every((id) =>
    state.permit.measurementDevicesOrMethods.map((item) => item.id).includes(id),
  );
}

export const measurementCategoryTierStatuses = [
  'MEASUREMENT_Category',
  'MEASUREMENT_Measured_Emissions',
  'MEASUREMENT_Applied_Standard',
] as const;

export const measurementStaticStatuses = [
  'MEASUREMENT_Description',
  'MEASUREMENT_Emission',
  'MEASUREMENT_Reference',
  'MEASUREMENT_Gasflow',
  'MEASUREMENT_Biomass',
  'MEASUREMENT_Corroborating',
] as const;

export type MeasurementStatuses =
  | 'MEASUREMENT_Category_Tier'
  | typeof measurementStaticStatuses[number]
  | typeof measurementCategoryTierStatuses[number];

/** Returns true if reference state is valid and all ids used in stream category exist */
function isMeasurementCategoryValid(state: PermitApplicationState, index: number): boolean {
  return (
    !!state.permit.sourceStreams.find(
      (stream) =>
        stream.id ===
        (state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach).sourceStreamCategoryAppliedTiers[
          index
        ]?.sourceStreamCategory?.sourceStream,
    ) &&
    (state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.emissionSources.every((id) =>
      state.permit.emissionSources.map((source) => source.id).includes(id),
    ) &&
    (state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.emissionPoints.every((id) =>
      state.permit.emissionPoints.map((point) => point.id).includes(id),
    )
  );
}
