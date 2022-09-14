import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-email-confirmed',
  template: `
    <govuk-panel>Email address confirmed</govuk-panel>

    <p class="govuk-body">You can continue to create a UK ETS reporting sign in.</p>

    <a routerLink="../user/contact-details" govukButton>Continue</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailConfirmedComponent {}
