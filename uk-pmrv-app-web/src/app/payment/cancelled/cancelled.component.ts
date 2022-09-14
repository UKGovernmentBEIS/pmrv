import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { first, map, Observable, pluck } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { PaymentCancelledRequestActionPayload } from 'pmrv-api';

import { PaymentStore } from '../store/payment.store';

@Component({
  selector: 'app-cancelled',
  template: `
    <ng-container *ngIf="details$ | async as details">
      <app-page-heading>
        Payment task cancelled
        <span class="govuk-body govuk-!-display-block govuk-!-margin-top-2">{{
          creationDate$ | async | govukDate
        }}</span>
      </app-page-heading>
      <h2 app-summary-header class="govuk-heading-m">Details</h2>
      <dl govuk-summary-list>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Payment status</dt>
          <dd govukSummaryListRowValue>{{ details.status | paymentStatus }}</dd>
        </div>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Reason</dt>
          <dd govukSummaryListRowValue class="pre-wrap">{{ details.cancellationReason }}</dd>
        </div>
      </dl>
    </ng-container>
    <a govukLink routerLink="/dashboard">Return to: Dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class CancelledComponent implements OnInit {
  details$ = this.store.pipe(
    first(),
    map((state) => state.actionPayload as PaymentCancelledRequestActionPayload),
  );
  creationDate$: Observable<string> = this.store.pipe(pluck('creationDate'));

  constructor(private readonly store: PaymentStore, private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
