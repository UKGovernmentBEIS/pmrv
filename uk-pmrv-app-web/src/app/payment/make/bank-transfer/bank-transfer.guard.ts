import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable, pluck } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';

@Injectable({ providedIn: 'root' })
export class BankTransferGuard implements CanActivate {
  constructor(private readonly store: PaymentStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      pluck('selectedPaymentMethod'),
      map(
        (selectedPaymentMethod) =>
          selectedPaymentMethod === 'BANK_TRANSFER' ||
          this.router.parseUrl(`/payment/${route.paramMap.get('taskId')}/make/details`),
      ),
    );
  }
}
