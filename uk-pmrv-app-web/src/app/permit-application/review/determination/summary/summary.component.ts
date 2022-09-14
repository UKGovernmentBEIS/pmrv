import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { Observable, pluck } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task [notification]="notification">
      <app-page-heading>Permit determination</app-page-heading>

      <app-determination-summary-details
        [decision]="decision$ | async"
        [reason]="reason$ | async"
        [activationDate]="activationDate$ | async"
        [officialNotice]="officialNotice$ | async"
        [annualEmissionsTargets]="annualEmissionsTargets$ | async"
        [changePerStage]="true"
        cssClass="summary-list--edge-border"
      ></app-determination-summary-details>

      <a govukLink routerLink="../..">Return to: Permit determination</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  decision$ = this.store.pipe(pluck('determination', 'type'));
  reason$ = this.store.pipe(pluck('determination', 'reason'));
  activationDate$ = this.store.pipe(pluck('determination', 'activationDate')) as Observable<string>;
  officialNotice$ = this.store.pipe(pluck('determination', 'officialNotice')) as Observable<string>;
  annualEmissionsTargets$ = this.store.pipe(pluck('determination', 'annualEmissionsTargets')) as Observable<{
    [key: string]: number;
  }>;

  constructor(private readonly router: Router, private readonly store: PermitApplicationStore) {}
}
