import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { RequestActionsService } from 'pmrv-api';

import { SharedStore } from '../shared/store/shared.store';

@Injectable({ providedIn: 'root' })
export class PermitRevocationActionGuard implements CanActivate {
  constructor(
    private readonly store: PermitRevocationStore,
    private readonly sharedStore: SharedStore,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return this.requestActionsService.getRequestActionByIdUsingGET(actionId).pipe(
      tap((requestAction) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          ...requestAction.payload,
          requestActionId: requestAction.id,
          requestActionType: requestAction.type,
          isEditable: false,
          creationDate: requestAction.creationDate,
        } as PermitRevocationState);

        this.sharedStore.reset();
        this.sharedStore.setState({ ...this.sharedStore.getState(), accountId: requestAction.requestAccountId });
      }),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.sharedStore.reset();
    return true;
  }
}
