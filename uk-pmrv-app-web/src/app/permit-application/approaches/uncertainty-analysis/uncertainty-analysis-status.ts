import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { PermitApplicationState } from '../../store/permit-application.state';

export function uncertaintyAnalysisStatus(state: PermitApplicationState): TaskItemStatus {
  const uncertaintyAnalysis = state.permit?.uncertaintyAnalysis;
  return state.permitSectionsCompleted.uncertaintyAnalysis?.[0]
    ? 'complete'
    : uncertaintyAnalysis?.exist !== undefined
    ? 'in progress'
    : 'not started';
}
