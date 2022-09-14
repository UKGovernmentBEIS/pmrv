import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable, pluck } from 'rxjs';

import { AccountViewService } from 'pmrv-api';

import { accountFinalStatuses } from '../../core/accountFinalStatuses';

@Injectable({
  providedIn: 'root',
})
export class AccountStatusGuard implements CanActivate {
  constructor(private readonly accountViewService: AccountViewService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const accountId = Number(route.paramMap.get('accountId'));

    return this.accountViewService.getAccountByIdUsingGET(accountId).pipe(
      pluck('status'),
      map((status) => accountFinalStatuses(status) || this.router.parseUrl(`/accounts/${accountId}`)),
    );
  }
}
