import moment from 'moment';

import { PermitSurrenderReviewDecision } from 'pmrv-api';

import { PermitSurrenderState } from '../../store/permit-surrender.state';
import { DecisionStatus, DeterminationStatus } from './review';

export function resolveDecisionStatus(state: PermitSurrenderState): DecisionStatus {
  return !state.reviewDecision ? 'undecided' : state.reviewDecision.type === 'ACCEPTED' ? 'accepted' : 'rejected';
}

export function resolveDeterminationStatus(state: PermitSurrenderState): DeterminationStatus {
  const reviewDeterminationCompleted = state.reviewDeterminationCompleted;
  if (!reviewDeterminationCompleted) {
    return needsReview(state) ? 'needs review' : 'undecided';
  }

  const reviewDeterminationType = state.reviewDetermination?.type;
  switch (reviewDeterminationType) {
    case 'GRANTED':
      return needsReview(state) ? 'needs review' : 'granted';
    case 'REJECTED':
      return 'rejected';
    case 'DEEMED_WITHDRAWN':
      return 'deemed withdrawn';
    default:
      return needsReview(state) ? 'needs review' : 'undecided';
  }
}
export const needsReview = (state: PermitSurrenderState): boolean => {
  const noticeDate = (state.reviewDetermination as any)?.noticeDate;
  if (!noticeDate) {
    return false;
  }
  const after28Days = moment().add(28, 'd').set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
  return !moment(new Date(noticeDate)).isAfter(after28Days);
};

export function resolveDeterminationCompletedUponDecision(
  newType: PermitSurrenderReviewDecision['type'],
  state: PermitSurrenderState,
): boolean {
  const reviewDeterminationType = state.reviewDetermination?.type;
  const reviewDeterminationCompleted = state.reviewDeterminationCompleted;

  if (!reviewDeterminationType) {
    return null;
  }

  if (reviewDeterminationType === 'DEEMED_WITHDRAWN') {
    return reviewDeterminationCompleted;
  }

  switch (newType) {
    case 'ACCEPTED':
      return reviewDeterminationType === 'GRANTED';
    case 'REJECTED':
      return reviewDeterminationType === 'REJECTED';
  }
}
