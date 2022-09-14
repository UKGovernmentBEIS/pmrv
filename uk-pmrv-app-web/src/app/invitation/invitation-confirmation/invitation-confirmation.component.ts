import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-regulator-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>You've successfully activated your user account</govuk-panel>
        <p class="govuk-body">
          When you sign in to the PMRV for the first time, you 'll be asked to set up two-factor authentication using
          the FreeOTP Authenticator app
        </p>
        <p class="govuk-body">You 'll be able to view guidance on how to download and use the app.</p>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">You are now able to sign in to PMRV</p>
        <a routerLink="." (click)="authService.login()" govukLink>Go to my dashboard</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvitationConfirmationComponent {
  constructor(readonly authService: AuthService) {}
}
