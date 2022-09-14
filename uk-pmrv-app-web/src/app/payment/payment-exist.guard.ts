import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PaymentMakeRequestTaskPayload } from 'pmrv-api';

import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentExistGuard implements CanActivate {
  constructor(private readonly store: PaymentStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          !(state.paymentDetails as PaymentMakeRequestTaskPayload)?.externalPaymentId ||
          !state.isEditable ||
          this.router.parseUrl(
            `/payment/${route.paramMap.get('taskId')}/make/confirmation?method=CREDIT_OR_DEBIT_CARD`,
          ),
      ),
    );
  }
}
