import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { Observable, of, switchMap } from 'rxjs';

import { AccountVerificationBodyService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../../../../error/concurrency-error/concurrency-error.service';
import { appointedVerificationBodyError } from '../../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class AppointGuard implements CanActivate {
  constructor(
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const accountId = Number(route.paramMap.get('accountId'));

    return this.accountVerificationBodyService
      .getVerificationBodyOfAccountUsingGET(accountId)
      .pipe(
        switchMap((vb) =>
          vb ? this.concurrencyErrorService.showError(appointedVerificationBodyError(accountId)) : of(true),
        ),
      );
  }
}
