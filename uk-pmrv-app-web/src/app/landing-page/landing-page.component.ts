import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable, pluck } from 'rxjs';

import { AuthService } from '../core/services/auth.service';
import { InstallationAccountApplicationStore } from '../installation-account-application/store/installation-account-application.store';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingPageComponent {
  isNotEnabled$: Observable<boolean> = this.authService.userStatus.pipe(
    first(),
    pluck('loginStatus'),
    map((loginStatus) => loginStatus !== 'ENABLED'),
  );

  constructor(public readonly authService: AuthService, public readonly store: InstallationAccountApplicationStore) {}
}
