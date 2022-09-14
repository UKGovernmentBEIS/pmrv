import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { CaExternalContactDTO } from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { saveNotFoundExternalContactError } from '../../errors/concurrency-error';
import { DetailsGuard } from '../details/details.guard';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate, Resolve<CaExternalContactDTO> {
  constructor(
    private readonly externalContactDetailsGuard: DetailsGuard,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.externalContactDetailsGuard
      .isExternalContactActive(route)
      .pipe(
        catchBadRequest(ErrorCode.EXTCONTACT1000, () =>
          this.concurrencyErrorService.showError(saveNotFoundExternalContactError),
        ),
      );
  }

  resolve(): CaExternalContactDTO {
    return this.externalContactDetailsGuard.resolve();
  }
}
