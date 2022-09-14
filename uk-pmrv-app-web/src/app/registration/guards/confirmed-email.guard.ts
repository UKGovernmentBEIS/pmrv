import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';

import { catchError, mapTo, Observable, of, tap, throwError } from 'rxjs';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { isBadRequest } from '../../error/business-errors';
import { UserRegistrationStore } from '../store/user-registration.store';

@Injectable({ providedIn: 'root' })
export class ConfirmedEmailGuard implements CanActivate {
  constructor(
    private store: UserRegistrationStore,
    private router: Router,
    private operatorUsersRegistrationService: OperatorUsersRegistrationService,
  ) {}

  canActivate(next: ActivatedRouteSnapshot): Observable<boolean> | boolean {
    const token = next.queryParamMap.get('token');

    if (token) {
      return this.operatorUsersRegistrationService.verifyUserRegistrationTokenUsingPOST({ token }).pipe(
        tap(({ email }) => this.store.setState({ ...this.store.getState(), email, token, isInvited: false })),
        mapTo(true),
        catchError((res: unknown) => {
          if (isBadRequest(res)) {
            this.router.navigate(['/registration/invalid-link'], { queryParams: { code: res.error.code } });

            return of(false);
          } else {
            return throwError(() => res);
          }
        }),
      );
    } else if (this.store.getState().token) {
      return true;
    } else {
      this.router.navigateByUrl('/registration');

      return false;
    }
  }
}
