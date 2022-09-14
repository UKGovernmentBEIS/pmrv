import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-invalid-code',
  template: `
    <app-page-heading>Invalid code</app-page-heading>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <p class="govuk-body">Invalid code. Please try again.</p>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidCodeComponent {}
