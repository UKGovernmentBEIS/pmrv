import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-peer-review-decision-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Peer review complete"></govuk-panel>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">The review has been returned to the regulator.</p>
        <a govukLink routerLink="/dashboard"> Return to dashboard </a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}
