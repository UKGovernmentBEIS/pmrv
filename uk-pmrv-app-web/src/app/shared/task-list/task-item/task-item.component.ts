import { ChangeDetectionStrategy, Component, HostBinding, Input } from '@angular/core';

import { ReviewDeterminationStatus, ReviewGroupStatus } from '../../../permit-application/review/review';
import { DecisionStatus, FollowUpDecisionStatus } from '../../../tasks/permit-notification/core/section-status';
import { TaskItemStatus } from '../task-list.interface';
import { statusMap } from './status.map';

@Component({
  selector: 'li[app-task-item]',
  template: `
    <span class="app-task-list__task-name" [class.govuk-!-margin-bottom-3]="hasContent">
      <a *ngIf="link; else plainText" [routerLink]="link" govukLink>{{ linkText }}</a>
      <ng-template #plainText>
        <strong>{{ linkText }}</strong>
      </ng-template>
    </span>
    <govuk-tag *ngIf="status" [color]="status | tagColor" class="app-task-list__tag">
      {{ status | i18nSelect: statusMap }}
    </govuk-tag>
    <ng-content></ng-content>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskItemComponent {
  @Input() link: string;
  @Input() linkText: string;
  @Input() status:
    | TaskItemStatus
    | ReviewGroupStatus
    | ReviewDeterminationStatus
    | DecisionStatus
    | FollowUpDecisionStatus;
  @Input() hasContent: boolean;

  @HostBinding('class.app-task-list__item') readonly taskListItem = true;

  statusMap = statusMap;
}
