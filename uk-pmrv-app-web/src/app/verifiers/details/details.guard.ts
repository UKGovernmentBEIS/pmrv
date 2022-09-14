import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { ApplicationUserDTO, UsersService, VerifierUserDTO, VerifierUsersService } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundVerifierError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DetailsGuard implements CanActivate, Resolve<ApplicationUserDTO | VerifierUserDTO> {
  private verifier: ApplicationUserDTO | VerifierUserDTO;

  constructor(
    private readonly verifierUsersService: VerifierUsersService,
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  isVerifierActive(route: ActivatedRouteSnapshot): Observable<boolean> {
    return (
      route.paramMap.get('userId') === this.authService.userStatus.getValue().userId
        ? this.usersService.getCurrentUserUsingGET()
        : this.verifierUsersService.getVerifierUserByIdUsingGET(route.paramMap.get('userId'))
    ).pipe(
      tap((verifier) => (this.verifier = verifier)),
      map((verifier) => !!verifier),
    );
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.isVerifierActive(route).pipe(
      catchBadRequest(ErrorCode.AUTHORITY1006, () => this.concurrencyErrorService.showError(viewNotFoundVerifierError)),
    );
  }

  resolve(): ApplicationUserDTO | VerifierUserDTO {
    return this.verifier;
  }
}
