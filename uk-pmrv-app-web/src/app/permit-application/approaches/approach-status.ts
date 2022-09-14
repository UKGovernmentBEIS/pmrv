import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitApplicationState } from '../store/permit-application.state';

export function monitoringApproachesStatus(state: PermitApplicationState): TaskItemStatus {
  return !state.permitSectionsCompleted.monitoringApproachesPrepare?.[0]
    ? 'cannot start yet'
    : state.permitSectionsCompleted.monitoringApproaches?.[0]
    ? 'complete'
    : state.permitSectionsCompleted.monitoringApproachesPrepare?.[0] &&
      (state.permit.monitoringApproaches === undefined || Object.keys(state.permit.monitoringApproaches).length === 0)
    ? 'not started'
    : 'in progress';
}
