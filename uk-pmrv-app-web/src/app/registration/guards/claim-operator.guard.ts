import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { isBadRequest } from '../../error/business-errors';
import { UserRegistrationStore } from '../store/user-registration.store';

export type ClaimOperatorData = { accountInstallationName: string; roleCode: string };
@Injectable({ providedIn: 'root' })
export class ClaimOperatorGuard implements CanActivate, Resolve<ClaimOperatorData> {
  private claimOperatorData: ClaimOperatorData;

  constructor(
    private readonly router: Router,
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly store: UserRegistrationStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const token = route.queryParamMap.get('token');

    return token
      ? this.operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST({ token }).pipe(
          tap(
            (res) =>
              (this.claimOperatorData = {
                accountInstallationName: res.accountInstallationName,
                roleCode: res.roleCode,
              }),
          ),
          map((res) => {
            const pendingInvitationStatuses = [
              'PENDING_USER_REGISTRATION_NO_PASSWORD',
              'PENDING_USER_REGISTRATION',
              'PENDING_USER_ENABLE',
            ];

            if (pendingInvitationStatuses.includes(res.invitationStatus)) {
              this.store.setState({
                ...this.store.getState(),
                email: res.email,
                token,
                isInvited: true,
                userRegistrationDTO: {
                  ...this.store.getState().userRegistrationDTO,
                  ...{ email: res.email, firstName: res.firstName, lastName: res.lastName },
                },
                invitationStatus: res.invitationStatus,
              });

              switch (res.invitationStatus) {
                case 'PENDING_USER_REGISTRATION':
                case 'PENDING_USER_REGISTRATION_NO_PASSWORD':
                  this.router.navigate(['/registration/user/contact-details']);
                  break;
                case 'PENDING_USER_ENABLE':
                  this.router.navigate(['/registration/user/choose-password']);
                  break;
              }
            } else if (res.invitationStatus === 'ACCEPTED') {
              return true;
            }

            return false;
          }),
          catchError((res: unknown) => {
            if (isBadRequest(res)) {
              this.router.navigate(['/registration/invitation/invalid-link'], {
                queryParams: { code: res.error.code },
              });

              return of(false);
            } else {
              return throwError(() => res);
            }
          }),
        )
      : this.store.getState().token
      ? of(true)
      : of(this.router.parseUrl(''));
  }

  resolve(): ClaimOperatorData {
    return this.claimOperatorData;
  }
}
