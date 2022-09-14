import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { isValidForAmends } from '../review';

@Injectable({
  providedIn: 'root',
})
export class ReturnForAmendsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          !state.isRequestTask ||
          (state?.allowedRequestTaskActions?.includes('PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS') &&
            isValidForAmends(state)) ||
          this.router.parseUrl(`/`),
      ),
    );
  }
}
