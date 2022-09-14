import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { PaymentMakeRequestTaskPayload } from 'pmrv-api';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-bank-transfer',
  templateUrl: './bank-transfer.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class BankTransferComponent implements OnInit {
  readonly competentAuthority$ = this.store.pipe(map((state) => state.requestTaskItem?.requestInfo.competentAuthority));
  readonly makePaymentDetails$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
  );

  constructor(
    readonly store: PaymentStore,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onMarkAsPaid(): void {
    this.store.setState({
      ...this.store.getState(),
      markedAsPaid: true,
    });

    this.router.navigate(['../mark-paid'], { relativeTo: this.route });
  }
}
