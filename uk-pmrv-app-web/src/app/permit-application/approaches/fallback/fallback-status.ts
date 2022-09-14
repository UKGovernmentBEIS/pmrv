import { FallbackMonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

export function FALLBACKStatus(state: PermitApplicationState): TaskItemStatus {
  const tiers = (state.permit.monitoringApproaches?.FALLBACK as FallbackMonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers;

  const tiersStatuses =
    tiers?.length > 0 ? tiers.map((_, index) => FALLBACKCategoryTierStatus(state, index)) : ['not started'];

  return fallbackStaticStatuses.every((status) => state.permitSectionsCompleted[status]?.[0]) &&
    tiersStatuses.every((status) => status === 'complete')
    ? 'complete'
    : tiersStatuses.some((status) => status === 'needs review')
    ? 'needs review'
    : fallbackStaticStatuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
      tiersStatuses.some((status) => status === 'in progress' || status === 'complete')
    ? 'in progress'
    : 'not started';
}

/** Returns the status of source stream category applier tier */
export function FALLBACKCategoryTierStatus(state: PermitApplicationState, index: number): TaskItemStatus {
  return isFallbackCategoryValid(state, index)
    ? state.permitSectionsCompleted['FALLBACK_Category']?.[index]
      ? 'complete'
      : 'not started'
    : 'needs review';
}

/** Returns the status of source stream category applied tier subtask */
export function FALLBACKCategoryTierSubtaskStatus(
  state: PermitApplicationState,
  key: StatusKey,
  index: number,
): TaskItemStatus {
  switch (key) {
    case 'FALLBACK_Category':
      return state.permitSectionsCompleted[key]?.[index]
        ? isFallbackCategoryValid(state, index)
          ? 'complete'
          : 'needs review'
        : 'not started';
    default:
      return state.permitSectionsCompleted[key]?.[index] ? 'complete' : 'not started';
  }
}

export function areCategoryTierPrerequisitesMet(state: PermitApplicationState): boolean {
  return (
    state.permitSectionsCompleted.sourceStreams?.[0] &&
    state.permitSectionsCompleted.measurementDevicesOrMethods?.[0] &&
    state.permitSectionsCompleted.emissionSources?.[0]
  );
}

export const fallbackStaticStatuses = ['FALLBACK_Description', 'FALLBACK_Uncertainty'] as const;
export type FallbackStatuses = 'FALLBACK_Category_Tier' | typeof fallbackStaticStatuses[number] | 'FALLBACK_Category';

/** Returns true if reference state is valid and all ids used in stream category exist */
function isFallbackCategoryValid(state: PermitApplicationState, index: number): boolean {
  return (
    !!state.permit.sourceStreams.find(
      (stream) =>
        stream.id ===
        (state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach).sourceStreamCategoryAppliedTiers[
          index
        ]?.sourceStreamCategory?.sourceStream,
    ) &&
    (state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.emissionSources.every((id) =>
      state.permit.emissionSources.map((source) => source.id).includes(id),
    ) &&
    (state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach).sourceStreamCategoryAppliedTiers[
      index
    ]?.sourceStreamCategory?.measurementDevicesOrMethods.every((id) =>
      state.permit.measurementDevicesOrMethods.map((device) => device.id).includes(id),
    )
  );
}
