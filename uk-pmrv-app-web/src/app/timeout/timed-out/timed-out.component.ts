import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-timed-out',
  template: `
    <app-page-heading size="xl">Your session has timed out</app-page-heading>
    <p class="govuk-body">
      <ng-container *ngIf="idle$ | async; else sessionExpired">
        We have reset your session because you did not do anything for {{ idle$ | async | secondsToMinutes }} . We did
        this to keep your information secure.
      </ng-container>
      <ng-template #sessionExpired>
        We have reset your session because it expired. We did this to keep your information secure.
      </ng-template>
    </p>

    <button class="govuk-button" (click)="onSignInAgain()">Sign in again</button>

    <p class="govuk-body">
      If you don't want to start again, you can
      <a govukLink href="https://www.gov.uk/">return to GOV.UK</a>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimedOutComponent {
  idle$ = this.activatedRoute.queryParamMap.pipe(map((queryParamMap) => Number(queryParamMap.get('idle'))));

  constructor(private readonly activatedRoute: ActivatedRoute, private readonly authService: AuthService) {}

  onSignInAgain(): void {
    this.authService.login({ redirectUri: location.origin });
  }
}
