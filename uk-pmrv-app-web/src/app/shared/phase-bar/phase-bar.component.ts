import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'app-phase-bar',
  template: `
    <govuk-phase-banner phase="beta">
      <!-- FIXME: apply correct href when discussed with analysis -->
      <ng-container *ngIf="authService.isLoggedIn | async">
        This is a new service – your <a govukLink routerLink="feedback">feedback</a> will help us to improve it.
      </ng-container>

      <span *ngIf="authService.userProfile | async as user" class="logged-in-user">
        You are logged in as: <span class="govuk-!-font-weight-bold">{{ user.firstName }} {{ user.lastName }}</span>
      </span>
    </govuk-phase-banner>
  `,
  styles: [
    `
      .govuk-phase-banner__text {
        width: 100%;
      }

      .logged-in-user {
        float: right;
      }
    `,
  ],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PhaseBarComponent {
  constructor(readonly authService: AuthService) {}
}
