import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { ReviewGroupStatus } from '../review/review';
import { PermitApplicationState } from '../store/permit-application.state';

export function variationDetailsStatus(state: PermitApplicationState): TaskItemStatus {
  const aboutVariation = state?.permitVariationDetails;

  return state.permitVariationDetailsCompleted ? 'complete' : aboutVariation ? 'in progress' : 'not started';
}

export function variationDetailsReviewStatus(state: PermitApplicationState): ReviewGroupStatus {
  return state?.permitVariationDetailsReviewCompleted === true ? 'accepted' : 'undecided';
}
