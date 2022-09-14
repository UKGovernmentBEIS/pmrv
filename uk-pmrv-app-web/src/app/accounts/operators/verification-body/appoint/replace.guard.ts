import { HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { mapTo, Observable, tap } from 'rxjs';

import { AccountVerificationBodyService, VerificationBodyNameInfoDTO } from 'pmrv-api';

import { catchElseRethrow } from '../../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundOperatorError } from '../../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class ReplaceGuard implements CanActivate {
  private verificationBodyNameInfo: VerificationBodyNameInfoDTO;

  constructor(
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const accountId = Number(route.paramMap.get('accountId'));

    return this.accountVerificationBodyService.getVerificationBodyOfAccountUsingGET(accountId).pipe(
      tap((res) => (this.verificationBodyNameInfo = res)),
      mapTo(true),
      catchElseRethrow(
        (error) => error.status === HttpStatusCode.NotFound,
        () => this.concurrencyErrorService.showError(viewNotFoundOperatorError(accountId)),
      ),
    );
  }

  resolve(): VerificationBodyNameInfoDTO {
    return this.verificationBodyNameInfo;
  }
}
