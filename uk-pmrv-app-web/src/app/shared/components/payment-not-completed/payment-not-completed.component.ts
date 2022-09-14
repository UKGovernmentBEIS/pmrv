import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-payment-not-completed',
  template: ` <app-page-heading>The payment task must be closed before you can proceed</app-page-heading> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class PaymentNotCompletedComponent implements OnInit {
  constructor(private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
