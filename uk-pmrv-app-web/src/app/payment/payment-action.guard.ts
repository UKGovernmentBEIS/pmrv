import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { RequestActionsService } from 'pmrv-api';

import { PaymentState } from './store/payment.state';
import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentActionGuard implements CanActivate {
  constructor(
    private readonly store: PaymentStore,
    private readonly sharedStore: SharedStore,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return this.requestActionsService.getRequestActionByIdUsingGET(actionId).pipe(
      tap((requestAction) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          actionPayload: requestAction.payload,
          requestActionId: requestAction.id,
          isEditable: false,
          creationDate: requestAction.creationDate,
        } as PaymentState);

        this.sharedStore.reset();
        this.sharedStore.setState({ ...this.sharedStore.getState(), accountId: requestAction.requestAccountId });
      }),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.sharedStore.reset();
    return true;
  }
}
