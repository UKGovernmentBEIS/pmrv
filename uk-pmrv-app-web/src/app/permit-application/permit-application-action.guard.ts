import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { combineLatest, first, map, mapTo } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { AuthService } from '../core/services/auth.service';
import { SharedStore } from '../shared/store/shared.store';
import { PermitApplicationStore } from './store/permit-application.store';
import { getVariationRequestActionTypes } from './variation/variation-types';

@Injectable({ providedIn: 'root' })
export class PermitApplicationActionGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore,
    private readonly sharedStore: SharedStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly authService: AuthService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return combineLatest([
      this.requestActionsService.getRequestActionByIdUsingGET(actionId),
      this.authService.userStatus,
    ]).pipe(
      first(),
      map(([requestAction, userStatus]) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...(requestAction.payload as any),
          isEditable: false,
          requestActionType: requestAction.type,
          isRequestTask: false,
          isVariation: getVariationRequestActionTypes().some((type) => requestAction.type.includes(type)),
          assignable: false,
          actionId: actionId,
          creationDate: requestAction.creationDate,
          userViewRole: userStatus.roleType,
        });

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
