import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, UrlTree } from '@angular/router';

import { filter, mapTo, Observable } from 'rxjs';

import { CommonActionsStore } from './store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class ActionGuard implements CanActivate {
  constructor(private readonly store: CommonActionsStore) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const actionId = route.params['actionId'];
    if (actionId) {
      this.store.requestedAction(actionId);
    }
    return this.store.storeInitialized$.pipe(
      filter((init) => !!init),
      mapTo(true),
    );
  }
}
