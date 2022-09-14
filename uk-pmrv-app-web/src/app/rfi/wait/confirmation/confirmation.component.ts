import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-cancel-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Request for information cancelled"> </govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}
