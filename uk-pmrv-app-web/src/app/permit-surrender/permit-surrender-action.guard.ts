import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { SharedStore } from '../shared/store/shared.store';
import { PermitSurrenderState } from './store/permit-surrender.state';
import { PermitSurrenderStore } from './store/permit-surrender.store';

@Injectable({ providedIn: 'root' })
export class PermitSurrenderActionGuard implements CanActivate {
  constructor(
    private readonly store: PermitSurrenderStore,
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
        } as PermitSurrenderState);

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
