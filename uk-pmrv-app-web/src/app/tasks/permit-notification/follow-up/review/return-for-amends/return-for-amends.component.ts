import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { PermitNotificationService } from '../../../core/permit-notification.service';
import { SummaryList } from '../../model/model';

@Component({
  selector: 'app-follow-up-return-for-amends',
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class FollowUpReturnForAmendsComponent implements OnInit {
  isEditable$ = this.store.select('isEditable');
  isfollowUpReturnForAmendsSubmitted$ = new BehaviorSubject(false);

  data$ = this.permitNotificationService.getPayload().pipe(
    map((p: PermitNotificationFollowUpApplicationReviewRequestTaskPayload) => {
      const reviewDecision = p.reviewDecision;
      const { changesRequired, dueDate } = reviewDecision;
      const files = this.permitNotificationService.getDownloadUrlFiles(reviewDecision.files);
      return { changesRequired, dueDate, files };
    }),
  );

  followUpDecisionSummaryListMapper: Record<
    keyof { changesRequired: string; files: string[]; dueDate: string },
    SummaryList
  > = {
    changesRequired: { label: 'Changes required', order: 1, type: 'string' },
    files: { label: 'Uploaded files', order: 2, type: 'files', isArray: true },
    dueDate: { label: 'Changes due by', order: 3, type: 'date' },
  };

  constructor(
    public readonly route: ActivatedRoute,
    private readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    private readonly permitNotificationService: PermitNotificationService,
    private readonly backlinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();
  }

  onSubmit() {
    this.permitNotificationService
      .postSubmit('PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS', 'EMPTY_PAYLOAD')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.isfollowUpReturnForAmendsSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
