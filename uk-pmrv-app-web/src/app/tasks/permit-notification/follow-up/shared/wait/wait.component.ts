import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import {
  PermitNotificationFollowUpWaitForAmendsRequestTaskPayload,
  PermitNotificationWaitForFollowUpRequestTaskPayload,
} from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { PermitNotificationService } from '../../../core/permit-notification.service';
import { SummaryList } from '../../model/model';

@Component({
  selector: 'app-wait',
  templateUrl: './wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaitComponent {
  constructor(
    readonly route: ActivatedRoute,
    private readonly permitNotificationService: PermitNotificationService,
    readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {
    this.notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  }
  isEditable$ = this.store.select('isEditable');
  requestTaskType$ = this.store.requestTaskType$;
  notification: any;

  getPayload$: Observable<
    PermitNotificationWaitForFollowUpRequestTaskPayload | PermitNotificationFollowUpWaitForAmendsRequestTaskPayload
  > = this.permitNotificationService.getPayload();

  data$ = this.permitNotificationService.followUpData$;

  amendsData$ = this.permitNotificationService.amendsData$;
  amendsFiles$ = this.permitNotificationService.downloadUrlFiles$;

  amendsReviewDecisionData$ = this.permitNotificationService.amendsReviewDecisionData$;
  amendsReviewDecisionFiles$ = (
    this.permitNotificationService.getPayload() as Observable<PermitNotificationFollowUpWaitForAmendsRequestTaskPayload>
  ).pipe(map((payload) => this.permitNotificationService.getDownloadUrlFiles(payload.reviewDecision.files)));

  followUpSummaryListMapper: Record<
    keyof { followUpRequest: string; followUpResponseExpirationDate: string },
    SummaryList
  > = {
    followUpRequest: { label: 'Request from the regulator', order: 1, type: 'string' },
    followUpResponseExpirationDate: { label: 'Due date', order: 2, type: 'date', url: 'due-date' },
  };

  amendsSummaryListMapper: Record<keyof { followUpRequest: string; followUpResponse: string }, SummaryList> = {
    followUpRequest: { label: 'Request from the regulator', order: 1, type: 'string' },
    followUpResponse: { label: 'Operators response', order: 2, type: 'string' },
  };

  amendsReviewDecisionSummaryListMapper: Record<keyof { changesRequired: string; dueDate: string }, SummaryList> = {
    changesRequired: { label: 'Amends needed for follow up response', order: 1, type: 'string' },
    dueDate: { label: 'Due date', order: 3, type: 'date', url: 'due-date' },
  };
}
