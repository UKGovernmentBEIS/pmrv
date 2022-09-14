import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { ApplicationUserDTO, OperatorUserDTO } from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { DetailsGuard } from '../details/details.guard';
import { saveNotFoundOperatorError } from '../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate, Resolve<OperatorUserDTO | ApplicationUserDTO> {
  constructor(
    private readonly operatorDetailsGuard: DetailsGuard,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.operatorDetailsGuard
      .isOperatorActive(route)
      .pipe(
        catchBadRequest(ErrorCode.AUTHORITY1004, () =>
          this.concurrencyErrorService.showError(saveNotFoundOperatorError(Number(route.paramMap.get('accountId')))),
        ),
      );
  }

  resolve(): OperatorUserDTO | ApplicationUserDTO {
    return this.operatorDetailsGuard.resolve();
  }
}
