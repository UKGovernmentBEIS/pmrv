import { HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, UrlTree } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { catchElseRethrow } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { disabledVerificationBodyError, viewNotFoundVerificationBodyError } from '../../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class AddGuard implements CanActivate {
  constructor(
    private readonly verificationBodyService: VerificationBodiesService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const verificationBodyId = Number(route.paramMap.get('verificationBodyId'));

    return this.verificationBodyService.getVerificationBodyByIdUsingGET(verificationBodyId).pipe(
      map((response) => response.status !== 'DISABLED'),
      catchElseRethrow(
        (res) => res.status === HttpStatusCode.NotFound,
        () => this.concurrencyErrorService.showError(viewNotFoundVerificationBodyError),
      ),
      tap((canAccess) => {
        if (!canAccess) {
          this.concurrencyErrorService.showError(disabledVerificationBodyError);
        }
      }),
    );
  }
}
