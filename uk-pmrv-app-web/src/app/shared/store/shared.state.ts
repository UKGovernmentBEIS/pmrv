import { PeerReviewDecision } from 'pmrv-api';

export interface SharedState {
  accountId?: number;
  peerReviewDecision?: {
    type?: PeerReviewDecision['type'];
    notes?: string;
  };
}

export const initialState: SharedState = {
  accountId: undefined,
  peerReviewDecision: undefined,
};
