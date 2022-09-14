import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Returned to operator for amends"></govuk-panel>
        <p class="govuk-body">Notification text if needed (TBD)</p>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">Explanation text(TBD)</p>
        <a govukLink routerLink="/dashboard"> Return to dashboard </a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}
