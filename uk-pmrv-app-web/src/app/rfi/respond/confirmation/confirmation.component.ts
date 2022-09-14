import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-not-allowed',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Response sent to regulator"> </govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}
