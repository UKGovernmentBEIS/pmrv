import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';

@Injectable({ providedIn: 'root' })
export class MarkPaidGuard implements CanActivate {
  constructor(private readonly store: PaymentStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          (state.selectedPaymentMethod === 'BANK_TRANSFER' && state.markedAsPaid) ||
          this.router.parseUrl(`/payment/${route.paramMap.get('taskId')}/make/details`),
      ),
    );
  }
}
