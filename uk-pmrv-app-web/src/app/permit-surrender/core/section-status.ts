import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitSurrenderState } from '../store/permit-surrender.state';

export function resolveApplyStatus(state: PermitSurrenderState): TaskItemStatus {
  const permitSurrender = state.permitSurrender;
  return state.sectionsCompleted?.SURRENDER_APPLY
    ? 'complete'
    : permitSurrender?.stopDate !== undefined
    ? 'in progress'
    : 'not started';
}

export function resolveSubmitStatus(state: PermitSurrenderState): TaskItemStatus {
  const applyStatus = resolveApplyStatus(state);
  return applyStatus === 'complete' ? 'not started' : 'cannot start yet';
}
