import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'app-success',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="You've successfully created a user account"></govuk-panel>
        <p class="govuk-body">We have sent an email with your user account details.</p>
        <p class="govuk-body">
          When you sign in to the UK ETS reporting service for the first time, you'll be asked to set up two factor
          authentication using the FreeOTP Authenticator app. You'll be able to view guidance on how to download and use
          the app.
        </p>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">
          You can sign in to the UK ETS reporting service and apply to create a new installation account.
        </p>
        <button (click)="authService.login({})" govukButton>Sign in</button>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SuccessComponent implements OnInit {
  constructor(
    readonly authService: AuthService,
    private readonly backLinkService: BackLinkService,
    private readonly store: UserRegistrationStore,
  ) {
    store.reset();
  }

  ngOnInit(): void {
    this.backLinkService.hide();
  }
}
