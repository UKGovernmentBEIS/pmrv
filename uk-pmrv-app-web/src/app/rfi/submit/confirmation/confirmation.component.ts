import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-submit-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Your request for information has been sent to the operator"> </govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}
