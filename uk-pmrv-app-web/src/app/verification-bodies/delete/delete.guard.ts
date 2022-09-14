import { HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { catchElseRethrow } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { saveNotFoundVerificationBodyError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate {
  constructor(
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.verificationBodiesService
      .getVerificationBodyByIdUsingGET(Number(route.paramMap.get('verificationBodyId')))
      .pipe(
        map((body) => !!body),
        catchElseRethrow(
          (res) => res.status === HttpStatusCode.NotFound,
          () => this.concurrencyErrorService.showError(saveNotFoundVerificationBodyError),
        ),
      );
  }
}
