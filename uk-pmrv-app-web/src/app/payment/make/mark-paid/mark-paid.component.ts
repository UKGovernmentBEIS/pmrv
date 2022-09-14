import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-mark-paid',
  template: `
    <app-page-heading>Are you sure you want to mark this payment as paid?</app-page-heading>
    <div class="govuk-button-group" *ngIf="(store.isEditable$ | async) === true">
      <button (click)="onComplete()" appPendingButton govukButton type="button">Confirm and complete</button>
    </div>
    <app-return-link [state]="store | async" returnLink="../details"></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class MarkPaidComponent implements OnInit {
  constructor(
    readonly store: PaymentStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onComplete(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postMarkAsPaid({
            ...state,
            completed: true,
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { method: 'BANK_TRANSFER' } }),
      );
  }
}
