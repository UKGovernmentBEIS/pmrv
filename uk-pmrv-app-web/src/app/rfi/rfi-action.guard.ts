import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { SharedStore } from '../shared/store/shared.store';
import { RfiStore } from './store/rfi.store';

@Injectable({ providedIn: 'root' })
export class RfiActionGuard implements CanActivate {
  constructor(
    private readonly store: RfiStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly sharedStore: SharedStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.requestActionsService.getRequestActionByIdUsingGET(Number(route.paramMap.get('actionId'))).pipe(
      tap((requestAction) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...requestAction.payload,
          actionId: requestAction.id,
          isEditable: false,
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
