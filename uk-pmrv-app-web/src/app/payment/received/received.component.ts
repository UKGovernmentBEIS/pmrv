import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { PaymentStore } from '../store/payment.store';

@Component({
  selector: 'app-received',
  template: `
    <ng-container *ngIf="store | async as state">
      <app-page-heading>
        Payment marked as received
        <span class="govuk-body govuk-!-display-block govuk-!-margin-top-2">{{ state.creationDate | govukDate }}</span>
      </app-page-heading>
      <app-payment-summary [details]="state.actionPayload" [isActionView]="true">
        <h2 app-summary-header class="govuk-heading-m">Details</h2>
      </app-payment-summary>
    </ng-container>
    <a govukLink routerLink="/dashboard">Return to: Dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class ReceivedComponent implements OnInit {
  constructor(readonly store: PaymentStore, private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
