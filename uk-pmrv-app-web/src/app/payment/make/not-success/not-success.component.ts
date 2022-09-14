import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-not-success',
  template: `
    <app-page-heading>{{ message$ | async }}</app-page-heading>
    <a govukLink routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotSuccessComponent {
  message$ = this.route.queryParams.pipe(map((params) => params?.message));

  constructor(readonly store: PaymentStore, private readonly route: ActivatedRoute) {}
}
