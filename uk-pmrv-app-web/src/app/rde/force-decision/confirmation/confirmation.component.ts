import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Extension request {{ decision === 'ACCEPTED' ? 'approved' : 'rejected' }}"> </govuk-panel>
      </div>
    </div>
    <div class="govuk-!-margin-top-6">
      <h2 class="govuk-heading-m">What happens next</h2>
      <ng-container *ngIf="decision === 'ACCEPTED'; else extensionRejected">
        <p class="govuk-body">
          The determination deadline has been updated to
          {{ (store | async)?.rdeResponsePayload?.proposedDueDate | govukDate }}
        </p>
      </ng-container>
      <ng-template #extensionRejected>
        <p class="govuk-body">Explanation text</p>
      </ng-template>
    </div>
    <a govukLink routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  decision = this.router.getCurrentNavigation().extras?.state?.decision;

  constructor(private readonly router: Router, readonly store: RdeStore) {}
}
