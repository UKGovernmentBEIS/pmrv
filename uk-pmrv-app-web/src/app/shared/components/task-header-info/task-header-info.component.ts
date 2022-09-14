import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-task-header-info',
  template: `
    <div class="govuk-!-margin-top-2">
      <p class="govuk-body"><strong>Assigned to:</strong> {{ assignee }}</p>
    </div>
    <ng-container *ngIf="daysRemaining !== undefined && daysRemaining !== null">
      <div class="govuk-!-margin-top-2">
        <p class="govuk-body"><strong>Days Remaining:</strong> {{ daysRemaining | daysRemaining }}</p>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskHeaderInfoComponent {
  @Input() assignee: string;
  @Input() daysRemaining: number;
}