import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, pluck } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { CommonTasksStore } from '../../../../store/common-tasks.store';

@Component({
  selector: 'app-follow-up-review-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <app-notify-operator
        [taskId]="taskId$ | async"
        [accountId]="accountId$ | async"
        confirmationMessage="The Permit Notification has been completed"
        requestTaskActionType="PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION"
      ></app-notify-operator>
    </div>
  `,
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReviewNotifyOperatorComponent implements OnInit {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.pipe(pluck('requestTaskItem', 'requestInfo', 'accountId'));

  constructor(
    private readonly route: ActivatedRoute,
    private store: CommonTasksStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
