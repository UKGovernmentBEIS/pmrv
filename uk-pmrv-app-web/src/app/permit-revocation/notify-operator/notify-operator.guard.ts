import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { checkForReviewStatus } from '@permit-revocation/core/section-status';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard implements CanActivate {
  constructor(private readonly store: PermitRevocationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
      route.data?.requestTaskActionType;

    return this.store.pipe(
      map((state) => {
        return (
          (state.allowedRequestTaskActions.includes(requestTaskActionType) &&
            this.sectionStatus(state, requestTaskActionType)) ||
          this.router.parseUrl(this.returnUrl(requestTaskActionType, route.paramMap.get('taskId')))
        );
      }),
    );
  }

  sectionStatus(
    state: PermitRevocationState,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): boolean {
    switch (requestTaskActionType) {
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
        return state?.sectionsCompleted.REVOCATION_APPLY && checkForReviewStatus(state);
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
        return !!state?.reason;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION':
        return state.cessationCompleted;
    }
  }

  returnUrl(requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'], taskId: string): string {
    switch (requestTaskActionType) {
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
        return `/permit-revocation/${taskId}`;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
        return `/permit-revocation/${taskId}/wait-for-appeal`;
    }
  }
}
