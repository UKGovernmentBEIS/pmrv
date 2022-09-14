import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-application-submitted',
  template: `
    <govuk-panel>Application submitted</govuk-panel>

    <p class="govuk-body">We have sent you a confirmation email.</p>

    <h3 class="govuk-heading-m">What happens next</h3>

    <p class="govuk-body">We’ve sent your application to {{ competentAuthority$ | async | competentAuthority }}</p>
    <p class="govuk-body">They will contact you either to confirm your application, or to ask for more information.</p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationSubmittedComponent {
  competentAuthority$ = this.store.select('competentAuthority');

  constructor(private readonly store: PermitApplicationStore) {}
}
