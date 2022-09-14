import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable, of } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';

@Injectable({ providedIn: 'root' })
export class ConfirmationGuard implements CanActivate {
  constructor(private readonly store: PaymentStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const paymentMethod = route.queryParams?.method;
    const returnUrl = `/payment/${route.paramMap.get('taskId')}/make/details`;

    switch (paymentMethod) {
      case 'BANK_TRANSFER':
        return this.store.pipe(
          map(
            (state) =>
              (state.selectedPaymentMethod === 'BANK_TRANSFER' && state.markedAsPaid && state.completed) ||
              this.router.parseUrl(returnUrl),
          ),
        );
      case 'CREDIT_OR_DEBIT_CARD':
        return of(true);
      default:
        return of(this.router.parseUrl(returnUrl));
    }
  }
}
