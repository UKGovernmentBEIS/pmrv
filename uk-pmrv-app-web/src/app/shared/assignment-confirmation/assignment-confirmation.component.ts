import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-assignment-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <ng-container *ngIf="user; else unassigned">
          <govuk-panel title="The task has been reassigned to">{{ user }}</govuk-panel>
          <h3 class="govuk-heading-m">What happens next</h3>
          <p class="govuk-body">The task will appear in the dashboard of the person to whom it has been assigned to</p>
        </ng-container>
        <ng-template #unassigned>
          <govuk-panel title="This task has been unassigned"></govuk-panel>
          <h3 class="govuk-heading-m">What happens next</h3>
          <p class="govuk-body">The task will appear in the unassigned tab of your dashboard</p>
        </ng-template>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignmentConfirmationComponent {
  @Input() user?: string;
}
