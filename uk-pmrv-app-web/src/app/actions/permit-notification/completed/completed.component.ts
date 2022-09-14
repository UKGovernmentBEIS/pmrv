import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable, pluck } from 'rxjs';

import { SummaryList } from '@permit-notification/follow-up/model/model';

import { PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload } from 'pmrv-api';

import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent {
  constructor(readonly route: ActivatedRoute, private readonly permitNotificationService: PermitNotificationService) {}

  decisionDetailsSummaryListMapper: Record<
    keyof { type: string; changesRequired: string; dueDate: string; notes: string },
    SummaryList
  > = {
    type: { label: 'Decision status', order: 1, type: 'string' },
    changesRequired: { label: 'Changes required', order: 2, type: 'string' },
    dueDate: { label: 'Changes due by', order: 3, type: 'date' },
    notes: { label: 'Notes', order: 4, type: 'string' },
  };
  decisionDetailsData$ = this.permitNotificationService.followUpReviewDecisionData$;
  decisionDetailsFiles$ = this.permitNotificationService.downloadUrlFiles$;

  followUpResponseDetailsSummaryListMapper: Record<
    keyof {
      request: string;
      responseExpirationDate: string;
      responseSubmissionDate: string;
      response: string;
    },
    SummaryList
  > = {
    request: { label: 'Request from the regulator', order: 1, type: 'string' },
    responseExpirationDate: { label: 'Due date', order: 2, type: 'date' },
    responseSubmissionDate: { label: 'Submission date', order: 3, type: 'date' },
    response: { label: 'Operators response', order: 4, type: 'string' },
  };
  followUpResponseDetailsData$ = this.permitNotificationService.followUpResponseDetailsData$;
  followUpResponseDetailsFiles$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>
  ).pipe(map((payload) => this.permitNotificationService.getDownloadUrlFiles(payload.responseFiles)));

  usersInfo$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>
  ).pipe(pluck('usersInfo'));

  signatory$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload>
  ).pipe(pluck('reviewDecisionNotification', 'signatory'));

  operators$: Observable<string[]> = combineLatest([this.usersInfo$, this.signatory$]).pipe(
    map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
  );
}
