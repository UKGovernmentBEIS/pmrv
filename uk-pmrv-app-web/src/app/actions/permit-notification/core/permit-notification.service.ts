import { Injectable } from '@angular/core';

import { filter, map, Observable, pluck } from 'rxjs';

import {
  PermitNotificationApplicationReviewRequestTaskPayload,
  PermitNotificationApplicationSubmittedRequestActionPayload,
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload,
  PermitNotificationFollowUpResponseSubmittedRequestActionPayload,
  PermitNotificationFollowUpReturnedForAmendsRequestActionPayload,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class PermitNotificationService {
  constructor(private readonly store: CommonActionsStore) {}

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  get notification$() {
    return this.getPayload().pipe(map((p) => p.permitNotification));
  }

  get downloadUrlFiles$(): Observable<{ downloadUrl: string; fileName: string }[]> {
    return this.documents$.pipe(map((documents) => this.getDownloadUrlFiles(documents || [])));
  }

  get reviewDecision$() {
    return this.getPayload().pipe(map((p: PermitNotificationApplicationReviewRequestTaskPayload) => p.reviewDecision));
  }

  get followUpData$() {
    return this.getPayload().pipe(
      map(({ response, request }: PermitNotificationFollowUpResponseSubmittedRequestActionPayload) => ({
        response,
        request,
      })),
    );
  }

  get followUpReviewDecisionData$() {
    return this.getPayload().pipe(
      pluck('reviewDecision'),
      map(
        ({
          type,
          changesRequired,
          dueDate,
          files,
          notes,
        }: PermitNotificationFollowUpApplicationReviewRequestTaskPayload['reviewDecision']) => ({
          type: type === 'ACCEPTED' ? 'Accepted' : 'Operator amends needed',
          changesRequired,
          dueDate,
          files,
          notes,
        }),
      ),
    );
  }

  get followUpResponseDetailsData$() {
    return this.getPayload().pipe(
      map(
        ({
          response,
          request,
          responseExpirationDate,
          responseSubmissionDate,
          responseFiles,
        }: PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload) => ({
          response,
          request,
          responseExpirationDate,
          responseSubmissionDate,
          responseFiles,
        }),
      ),
    );
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const permitNotificationAttachments = this.attachments || [];

    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: permitNotificationAttachments[id],
      })) ?? []
    );
  }

  private get attachments() {
    const payload = this.store.getValue().action.payload;
    switch (payload.payloadType) {
      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD':
        return (<PermitNotificationApplicationSubmittedRequestActionPayload>payload).permitNotificationAttachments;
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD':
        return (<PermitNotificationFollowUpResponseSubmittedRequestActionPayload>payload).responseAttachments;
      case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD':
        return (<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>payload)
          .responseAttachments;
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD':
        return (<PermitNotificationFollowUpReturnedForAmendsRequestActionPayload>payload)?.amendAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }

  private get documents$(): Observable<string[]> {
    return this.store.payload$.pipe(
      filter((payload) => !!payload?.payloadType),
      map((payload) => {
        switch (payload.payloadType) {
          case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD':
            return (<PermitNotificationApplicationSubmittedRequestActionPayload>payload).permitNotification.documents;
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD':
            return (<PermitNotificationFollowUpResponseSubmittedRequestActionPayload>payload).responseFiles;
          case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD':
            return (<
              PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload['reviewDecision']
            >payload.reviewDecision)?.files;
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD':
            return (<PermitNotificationFollowUpReturnedForAmendsRequestActionPayload>payload)?.amendFiles;
          default:
            throw Error('Unhandled action payload type: ' + payload.payloadType);
        }
      }),
    );
  }
}
