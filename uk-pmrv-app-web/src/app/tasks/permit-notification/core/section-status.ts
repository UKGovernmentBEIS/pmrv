import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  PermitNotificationApplicationSubmitRequestTaskPayload,
  PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpRequestTaskPayload,
  PermitNotificationReviewDecision,
} from 'pmrv-api';

export type StatusKey =
  | 'DETAILS_CHANGE'
  | 'SUBMIT'
  | 'FOLLOW_UP'
  | 'FOLLOW_UP_SUBMIT'
  | 'REVIEW_FOLLOW_UP'
  | 'FOLLOW_UP_AMENDS'
  | 'FOLLOW_UP_AMENDS_SUBMIT';

export type ReviewSectionKey = 'DETAILS_CHANGE' | 'FOLLOW_UP';

export type DecisionStatus = 'undecided' | 'accepted' | 'rejected';

export type FollowUpDecisionStatus = 'undecided' | 'accepted' | 'operator to amend';

export function resolveDetailsChangeStatus(
  state: PermitNotificationApplicationSubmitRequestTaskPayload,
): TaskItemStatus {
  return state?.sectionsCompleted['DETAILS_CHANGE']
    ? 'complete'
    : state?.permitNotification
    ? 'in progress'
    : 'not started';
}

export function resolveSubmitStatus(state: PermitNotificationApplicationSubmitRequestTaskPayload): TaskItemStatus {
  return state?.sectionsCompleted['DETAILS_CHANGE'] ? 'not started' : 'cannot start yet';
}

export function isWizardComplete(state: PermitNotificationApplicationSubmitRequestTaskPayload): boolean {
  return state && state?.permitNotification && !!state?.permitNotification?.description;
}

export function resolveDecisionStatus(reviewDecision: PermitNotificationReviewDecision): DecisionStatus {
  return !reviewDecision ? 'undecided' : reviewDecision.type === 'ACCEPTED' ? 'accepted' : 'rejected';
}

export function resolveFollowUpStatus(state: PermitNotificationFollowUpRequestTaskPayload): TaskItemStatus {
  return state?.followUpResponse ? 'complete' : 'not started';
}

export function resolveFollowUpSubmitStatus(state: PermitNotificationFollowUpRequestTaskPayload): TaskItemStatus {
  return resolveFollowUpStatus(state) === 'complete' ? 'not started' : 'cannot start yet';
}

export function resolveFollowUpDecisionStatus(
  payload: PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
): FollowUpDecisionStatus {
  return !payload.reviewDecision?.type || payload.reviewSectionsCompleted?.RESPONSE === false
    ? 'undecided'
    : payload.reviewDecision.type === 'ACCEPTED'
    ? 'accepted'
    : 'operator to amend';
}

export function resolveFollowUpDetailsOfAmendsStatus(
  state: PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
): TaskItemStatus {
  return !state?.followUpSectionsCompleted['AMENDS_NEEDED'] ? 'not started' : 'complete';
}

export function resolveFollowUpAmendsSubmitStatus(
  state: PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
): TaskItemStatus {
  return resolveFollowUpStatus(state) === 'complete' && state?.followUpSectionsCompleted['AMENDS_NEEDED']
    ? 'not started'
    : 'cannot start yet';
}
