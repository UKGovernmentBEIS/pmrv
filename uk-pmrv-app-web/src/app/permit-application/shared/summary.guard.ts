import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../store/permit-application.store';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        if (!storeState.isRequestTask || !storeState.isEditable) {
          return true;
        } else if (route.data.statusKey) {
          return (
            storeState.permitSectionsCompleted?.[route.data.statusKey]?.[route.paramMap.get('index') || 0] ||
            this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
          );
        } else {
          return (
            storeState.permitSectionsCompleted?.[route.data.permitTask]?.[route.paramMap.get('index') ?? 0] ||
            this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
          );
        }
      }),
    );
  }
}
