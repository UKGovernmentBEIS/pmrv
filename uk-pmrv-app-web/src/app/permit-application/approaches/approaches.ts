import { PermitApplicationState } from '../store/permit-application.state';

export function isReviewUrl(type): boolean {
  return [
    'PERMIT_ISSUANCE_APPLICATION_REVIEW',
    'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
    'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
  ].includes(type);
}

export function deleteReturnUrl(
  state: PermitApplicationState,
  reviewGroupUrl: 'calculation' | 'fall-back' | 'measurement' | 'nitrous-oxide' | 'pfc',
): string {
  return state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
    ? `../review/${reviewGroupUrl}/../../..`
    : '../../..';
}
