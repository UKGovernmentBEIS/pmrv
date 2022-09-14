import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve, UrlTree } from '@angular/router';

import { mapTo, Observable, tap } from 'rxjs';

import { RequestActionPayload, RequestActionsService } from 'pmrv-api';

import { updateState } from '../functions/update-state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable({ providedIn: 'root' })
export class SummaryActionGuard implements CanActivate, Resolve<RequestActionPayload['payloadType']> {
  private type: RequestActionPayload['payloadType'];

  constructor(
    private readonly requestActionsService: RequestActionsService,
    private readonly store: InstallationAccountApplicationStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.requestActionsService.getRequestActionByIdUsingGET(Number(route.paramMap.get('actionId'))).pipe(
      tap((res) => updateState(this.store, res.payload)),
      tap((res) => (this.type = res.payload.payloadType)),
      mapTo(true),
    );
  }

  resolve(): RequestActionPayload['payloadType'] {
    return this.type;
  }
}
