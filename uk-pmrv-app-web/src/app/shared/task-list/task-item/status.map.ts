import { ReviewDeterminationStatus, ReviewGroupStatus } from '../../../permit-application/review/review';
import { TaskItemStatus } from '../task-list.interface';

export const statusMap: Record<TaskItemStatus | ReviewGroupStatus | ReviewDeterminationStatus, string> = {
  'not started': 'not started',
  'cannot start yet': 'cannot start yet',
  'in progress': 'in progress',
  incomplete: 'incomplete',
  complete: 'completed',
  'needs review': 'needs review',
  undecided: 'undecided',
  accepted: 'accepted',
  rejected: 'rejected',
  'operator to amend': 'operator to amend',
  granted: 'granted',
  'deemed withdrawn': 'deemed withdrawn',
};
