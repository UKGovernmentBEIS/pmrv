import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { CommonTasksStore } from '../../../store/common-tasks.store';

@Component({
  selector: 'app-follow-up-review-wait',
  template: `
    <app-base-task-container-component
      [header]="(route.data | async)?.pageTitle"
      [customContentTemplate]="customContentTemplate"
      expectedTaskType="PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW"
      [daysRemaining]="daysRemaining$ | async"
    >
    </app-base-task-container-component>

    <ng-template #customContentTemplate>
      <govuk-warning-text> Waiting for the regulator to make a determination </govuk-warning-text>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReviewWaitComponent {
  readonly daysRemaining$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.daysRemaining));
  constructor(readonly route: ActivatedRoute, readonly store: CommonTasksStore) {}
}
