import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { ApplicationUserDTO, OperatorUserDTO, OperatorUsersService, UsersService } from 'pmrv-api';

import { AuthService } from '../../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundOperatorError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DetailsGuard implements CanActivate, Resolve<OperatorUserDTO | ApplicationUserDTO> {
  private userData: OperatorUserDTO | ApplicationUserDTO;

  constructor(
    private readonly operatorService: OperatorUsersService,
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  isOperatorActive(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.authService.userStatus.pipe(
      first(),
      switchMap(({ userId }) =>
        userId === route.paramMap.get('userId')
          ? this.usersService.getCurrentUserUsingGET()
          : this.operatorService.getOperatorUserByIdUsingGET(
              Number(route.paramMap.get('accountId')),
              route.paramMap.get('userId'),
            ),
      ),
      tap((userData) => (this.userData = userData)),
      map((res) => !!res),
    );
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.isOperatorActive(route).pipe(
      catchBadRequest(ErrorCode.AUTHORITY1004, () =>
        this.concurrencyErrorService.showError(viewNotFoundOperatorError(Number(route.paramMap.get('accountId')))),
      ),
    );
  }

  resolve(): OperatorUserDTO | ApplicationUserDTO {
    return this.userData;
  }
}
