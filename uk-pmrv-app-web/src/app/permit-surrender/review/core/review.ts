import { PermitSurrenderReviewDetermination } from 'pmrv-api';

import { PermitSurrenderState } from '../../store/permit-surrender.state';

export type ReviewSectionKey = 'DECISION' | 'DETERMINATION';

export type DecisionStatus = 'undecided' | 'accepted' | 'rejected';
export type DeterminationStatus = 'undecided' | 'granted' | 'rejected' | 'deemed withdrawn' | 'needs review';

export const DeterminationTypeUrlMap: Record<PermitSurrenderReviewDetermination['type'], string> = {
  GRANTED: 'grant',
  REJECTED: 'reject',
  DEEMED_WITHDRAWN: 'deem-withdraw',
};

export function isGrantActionAllowed(state: PermitSurrenderState): boolean {
  return state.reviewDecision?.type === 'ACCEPTED';
}

export function isRejectActionAllowed(state: PermitSurrenderState): boolean {
  return state.reviewDecision?.type === 'REJECTED';
}
