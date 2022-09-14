import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { AuthorityManagePermissionDTO, RegulatorAuthoritiesService } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundRegulatorError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class PermissionsResolver implements Resolve<AuthorityManagePermissionDTO> {
  constructor(
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly authService: AuthService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<AuthorityManagePermissionDTO> {
    return this.authService.userStatus.pipe(
      first(),
      switchMap(({ userId }) =>
        userId === route.paramMap.get('userId')
          ? this.regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCaUsingGET()
          : this.regulatorAuthoritiesService.getRegulatorUserPermissionsByCaAndIdUsingGET(route.paramMap.get('userId')),
      ),
      catchBadRequest(ErrorCode.AUTHORITY1003, () =>
        this.concurrencyErrorService.showError(viewNotFoundRegulatorError),
      ),
    );
  }
}
