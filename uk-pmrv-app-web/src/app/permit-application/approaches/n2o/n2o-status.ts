import { N2OMonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

/* main task status */
export function N2OStatus(state: PermitApplicationState): TaskItemStatus {
  const tiers = (state.permit.monitoringApproaches?.N2O as N2OMonitoringApproach)?.sourceStreamCategoryAppliedTiers;

  const tiersStatuses =
    tiers?.length > 0 ? tiers.map((_, index) => N2OCategoryTierStatus(state, index)) : ['not started'];

  return n2oStaticStatuses.every((status) => state.permitSectionsCompleted[status]?.[0]) &&
    tiersStatuses.every((status) => status === 'complete')
    ? 'complete'
    : tiersStatuses.some((status) => status === 'needs review')
    ? 'needs review'
    : n2oStaticStatuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
      tiersStatuses.some((status) => status === 'in progress' || status === 'complete')
    ? 'in progress'
    : 'not started';
}

/** Returns the status of source stream category applier tier */
export function N2OCategoryTierStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  return isN2OCategoryValid(state, index) &&
    N2OCategoryTierSubtaskStatus(state, 'N2O_Measured_Emissions', index) !== 'needs review'
    ? n2oCategoryTierStatuses.every((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'complete'
      : n2oCategoryTierStatuses.some((status) => state.permitSectionsCompleted[status]?.[index])
      ? 'in progress'
      : 'not started'
    : 'needs review';
}

/** Returns the status of source stream category applied tier subtask */
export function N2OCategoryTierSubtaskStatus(
  state: PermitApplicationState,
  key: StatusKey,
  index: number,
): TaskItemStatus {
  switch (key) {
    case 'N2O_Category':
      return state.permitSectionsCompleted[key]?.[index]
        ? isN2OCategoryValid(state, index)
          ? 'complete'
          : 'needs review'
        : 'not started';
    case 'N2O_Applied_Standard':
      return state.permitSectionsCompleted[key]?.[index]
        ? 'complete'
        : N2OCategoryTierSubtaskStatus(state, 'N2O_Category', index) === 'complete'
        ? 'not started'
        : 'cannot start yet';
    case 'N2O_Measured_Emissions': {
      return !doN2OMeasuredEmissionsExist(state, index)
        ? !areN2OMeasuredEmissionsPrerequisitesMet(state, index)
          ? 'cannot start yet'
          : 'not started'
        : !areN2OMeasuredEmissionsDevicesValid(state, index)
        ? 'needs review'
        : !state.permitSectionsCompleted[key]?.[index]
        ? 'in progress'
        : 'complete';
    }
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

function doN2OMeasuredEmissionsExist(state: PermitApplicationState, index: number): boolean {
  return !!(state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers?.[index]
    ?.measuredEmissions;
}

export function areN2OMeasuredEmissionsPrerequisitesMet(state: PermitApplicationState, index: number): boolean {
  return (
    (!!(state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers?.[index]
      ?.measuredEmissions &&
      !!state.permit?.measurementDevicesOrMethods) ||
    (!!(state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers?.[index]
      ?.sourceStreamCategory &&
      state.permitSectionsCompleted.measurementDevicesOrMethods?.[0])
  );
}

export function areN2OMeasuredEmissionsDevicesValid(state: PermitApplicationState, index: number): boolean {
  const measuredEmissions = (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach)
    .sourceStreamCategoryAppliedTiers?.[index]?.measuredEmissions;

  return measuredEmissions?.measurementDevicesOrMethods.every((id) =>
    state.permit.measurementDevicesOrMethods.map((item) => item.id).includes(id),
  );
}

export const n2oCategoryTierStatuses = ['N2O_Category', 'N2O_Measured_Emissions', 'N2O_Applied_Standard'] as const;

export const n2oStaticStatuses = [
  'N2O_Description',
  'N2O_Emission',
  'N2O_Reference',
  'N2O_Operational',
  'N2O_Emissions',
  'N2O_Concentration',
  'N2O_Product',
  'N2O_Materials',
  'N2O_Gas',
] as const;

export type N2OStatuses =
  | 'N2O_Category_Tier'
  | typeof n2oStaticStatuses[number]
  | typeof n2oCategoryTierStatuses[number];

/** Returns true if reference state is valid and all ids used in stream category exist */
function isN2OCategoryValid(state: PermitApplicationState, index: number): boolean {
  return (
    !!state.permit.sourceStreams.find(
      (stream) =>
        stream.id ===
        (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers[index]
          ?.sourceStreamCategory?.sourceStream,
    ) &&
    (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.emissionSources.every((id) =>
      state.permit.emissionSources.map((source) => source.id).includes(id),
    ) &&
    (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.emissionPoints.every((id) =>
      state.permit.emissionPoints.map((point) => point.id).includes(id),
    )
  );
}
