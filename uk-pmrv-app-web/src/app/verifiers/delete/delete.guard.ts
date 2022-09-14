import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { ApplicationUserDTO, VerifierUserDTO } from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { DetailsGuard } from '../details/details.guard';
import { saveNotFoundVerifierError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate, Resolve<ApplicationUserDTO | VerifierUserDTO> {
  constructor(
    private readonly verifierDetailsGuard: DetailsGuard,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.verifierDetailsGuard
      .isVerifierActive(route)
      .pipe(
        catchBadRequest(ErrorCode.AUTHORITY1006, () =>
          this.concurrencyErrorService.showError(saveNotFoundVerifierError),
        ),
      );
  }

  resolve(): ApplicationUserDTO | VerifierUserDTO {
    return this.verifierDetailsGuard.resolve();
  }
}
