import { HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { VerificationBodiesService, VerificationBodyDTO } from 'pmrv-api';

import { catchElseRethrow } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundVerificationBodyError } from '../errors/concurrency-error';

@Injectable({
  providedIn: 'root',
})
export class DetailsGuard implements CanActivate, Resolve<VerificationBodyDTO> {
  private body: VerificationBodyDTO;

  constructor(
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.verificationBodiesService
      .getVerificationBodyByIdUsingGET(Number(route.paramMap.get('verificationBodyId')))
      .pipe(
        tap((response) => (this.body = response)),
        map((response) => !!response),
        catchElseRethrow(
          (res) => res.status === HttpStatusCode.NotFound,
          () => this.concurrencyErrorService.showError(viewNotFoundVerificationBodyError),
        ),
      );
  }

  resolve(): VerificationBodyDTO {
    return this.body;
  }
}
