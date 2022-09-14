import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-summary-container',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-full">
      <govuk-notification-banner *ngIf="notification" type="success">
        <h1 class="govuk-notification-banner__heading">Details updated</h1>
      </govuk-notification-banner>
      <app-page-heading>{{ (route.data | async)?.pageTitle }}</app-page-heading>
      <app-withdraw-summary></app-withdraw-summary>
      <a govukLink routerLink="../">Return to: Withdraw permit revocation</a>
    </div>
  </div>`,
  providers: [BackLinkService],

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawSummaryContainerComponent implements OnInit {
  notification: any;

  constructor(
    readonly route: ActivatedRoute,
    readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {
    this.notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  }

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
