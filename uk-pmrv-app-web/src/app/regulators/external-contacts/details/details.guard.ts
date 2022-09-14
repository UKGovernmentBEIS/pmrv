import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { mapTo, Observable, tap } from 'rxjs';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { viewNotFoundExternalContactError } from '../../errors/concurrency-error';

@Injectable({ providedIn: 'root' })
export class DetailsGuard implements CanActivate, Resolve<CaExternalContactDTO> {
  private externalContact: CaExternalContactDTO;

  constructor(
    private readonly caExternalContactsService: CaExternalContactsService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  isExternalContactActive(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.caExternalContactsService.getCaExternalContactByIdUsingGET(Number(route.paramMap.get('userId'))).pipe(
      tap((contact) => (this.externalContact = contact)),
      mapTo(true),
    );
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.isExternalContactActive(route).pipe(
      catchBadRequest(ErrorCode.EXTCONTACT1000, () =>
        this.concurrencyErrorService.showError(viewNotFoundExternalContactError),
      ),
    );
  }

  resolve(): CaExternalContactDTO {
    return this.externalContact;
  }
}
