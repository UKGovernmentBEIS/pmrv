import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { PermitApplicationState } from '../../store/permit-application.state';

export function INHERENT_CO2Status(state: PermitApplicationState): TaskItemStatus {
  return inherentCo2Statuses.every((status) => state.permitSectionsCompleted[status]?.[0])
    ? 'complete'
    : inherentCo2Statuses.some((status) => state.permitSectionsCompleted[status]?.[0])
    ? 'in progress'
    : 'not started';
}

export const inherentCo2Statuses = ['INHERENT_CO2_Description'] as const;
export type InherentCo2Status = typeof inherentCo2Statuses[number];
