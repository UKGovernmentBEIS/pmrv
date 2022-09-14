import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { StoreResolver } from '../../store-resolver/store-resolver';
import { getRequestTaskActionType, getRequestType } from './peer-review-decision-type-resolver';

@Injectable({
  providedIn: 'root',
})
export class PeerReviewDecisionGuard implements CanActivate {
  constructor(private storeResolver: StoreResolver, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const requestType = getRequestType(state.url);
    const requestTaskActionType: string = getRequestTaskActionType(requestType);

    const store = this.storeResolver.getStore(requestType);
    return store.pipe(
      map((state) => {
        let allowedRequestTaskActions;
        let returnUrl;

        switch (requestType) {
          case 'permit-application':
          case 'permit-revocation':
          case 'permit-surrender':
            allowedRequestTaskActions = state.allowedRequestTaskActions;
            returnUrl = `${requestType}/${route.paramMap.get('taskId')}/review`;
            break;
          case 'permit-notification':
            allowedRequestTaskActions = state.requestTaskItem.allowedRequestTaskActions;
            returnUrl = `tasks/${route.paramMap.get('taskId')}/${requestType}/peer-review`;
            break;
        }

        return allowedRequestTaskActions.includes(`${requestTaskActionType}`) || this.router.parseUrl(returnUrl);
      }),
    );
  }
}
