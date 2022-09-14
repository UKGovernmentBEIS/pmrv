import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { RegulatorUserDTO, RegulatorUsersService, UsersService } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundRegulatorError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DetailsResolver implements Resolve<RegulatorUserDTO> {
  constructor(
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<RegulatorUserDTO> {
    return this.authService.userStatus.pipe(
      first(),
      switchMap(({ userId }) =>
        userId === route.paramMap.get('userId')
          ? this.usersService.getCurrentUserUsingGET()
          : this.regulatorUsersService.getRegulatorUserByCaAndIdUsingGET(route.paramMap.get('userId')),
      ),
      catchBadRequest(ErrorCode.AUTHORITY1003, () =>
        this.concurrencyErrorService.showError(viewNotFoundRegulatorError),
      ),
    );
  }
}
