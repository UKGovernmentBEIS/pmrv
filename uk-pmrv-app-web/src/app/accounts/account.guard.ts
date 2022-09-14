import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate, Resolve } from '@angular/router';

import { first, map, Observable, tap } from 'rxjs';

import { AccountDetailsDTO, AccountViewService } from 'pmrv-api';

import { SharedStore } from '../shared/store/shared.store';

@Injectable({
  providedIn: 'root',
})
export class AccountGuard implements CanActivate, CanDeactivate<any>, Resolve<AccountDetailsDTO> {
  accountDetails: AccountDetailsDTO;

  constructor(private readonly accountViewService: AccountViewService, private readonly sharedStore: SharedStore) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.accountViewService.getAccountByIdUsingGET(Number(route.paramMap.get('accountId'))).pipe(
      first(),
      tap((accountDetails) => (this.accountDetails = accountDetails)),
      map((accountDetails) => !!accountDetails),
    );
  }

  canDeactivate(): boolean {
    this.sharedStore.reset();
    return true;
  }

  resolve(route: ActivatedRouteSnapshot): AccountDetailsDTO {
    const requestId = route.paramMap.get('request-id');
    this.sharedStore.reset();
    this.sharedStore.setState({
      ...this.sharedStore.getState(),
      accountId: requestId ? this.accountDetails.id : undefined,
    });
    return !requestId ? this.accountDetails : undefined;
  }
}
