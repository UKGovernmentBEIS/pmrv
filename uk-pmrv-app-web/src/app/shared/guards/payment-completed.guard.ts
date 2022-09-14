import { Injectable, Injector } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  PRIMARY_OUTLET,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { Store } from '@core/store';

import { RequestItemsService, RequestTaskDTO } from 'pmrv-api';

import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { PermitRevocationStore } from '../../permit-revocation/store/permit-revocation-store';
import { PermitSurrenderStore } from '../../permit-surrender/store/permit-surrender.store';
import { RdeStore } from '../../rde/store/rde.store';
import { RfiStore } from '../../rfi/store/rfi.store';

@Injectable({
  providedIn: 'root',
})
export class PaymentCompletedGuard implements CanActivate {
  constructor(
    private readonly requestItemsService: RequestItemsService,
    private readonly router: Router,
    private injector: Injector,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<true | UrlTree> {
    const waitForPaymentTypes: Array<RequestTaskDTO['type']> = [
      'PERMIT_ISSUANCE_TRACK_PAYMENT',
      'PERMIT_ISSUANCE_CONFIRM_PAYMENT',

      'PERMIT_SURRENDER_TRACK_PAYMENT',
      'PERMIT_SURRENDER_CONFIRM_PAYMENT',

      'PERMIT_REVOCATION_TRACK_PAYMENT',
      'PERMIT_REVOCATION_CONFIRM_PAYMENT',
    ];

    const tree = this.router.parseUrl(routerState.url);
    const segmentGroup = tree.root.children[PRIMARY_OUTLET];
    const segment = segmentGroup.segments;

    const redirectUrlPath = `/${segment[0].path}/${route.paramMap.get('taskId')}`;
    const redirectUrl =
      segment[0].path === 'permit-application' || segment[0].path === 'permit-surrender'
        ? this.router.parseUrl(redirectUrlPath.concat('/review/payment-not-completed'))
        : this.router.parseUrl(redirectUrlPath.concat('/payment-not-completed'));

    return this.store(segment[0].path).pipe(
      first(),
      map((state) => state.requestId),
      switchMap((requestId) => this.requestItemsService.getItemsByRequestUsingGET(requestId)),
      map((res) => !res.items.some((i) => waitForPaymentTypes.includes(i.taskType)) || redirectUrl),
    );
  }

  private store(type: string): Store<any> {
    switch (type) {
      case 'rde':
        return this.injector.get(RdeStore);
      case 'rfi':
        return this.injector.get(RfiStore);
      case 'permit-application':
        return this.injector.get(PermitApplicationStore);
      case 'permit-surrender':
        return this.injector.get(PermitSurrenderStore);
      case 'permit-revocation':
        return this.injector.get(PermitRevocationStore);
    }
  }
}
