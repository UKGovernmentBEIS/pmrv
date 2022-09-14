import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { AccountOperatorsUsersAuthoritiesInfoDTO, OperatorAuthoritiesService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class OperatorsGuard implements Resolve<AccountOperatorsUsersAuthoritiesInfoDTO> {
  constructor(private readonly operatorAuthoritiesService: OperatorAuthoritiesService) {}

  resolve(next: ActivatedRouteSnapshot): Observable<AccountOperatorsUsersAuthoritiesInfoDTO> {
    return this.operatorAuthoritiesService.getAccountOperatorAuthoritiesUsingGET(
      Number(next.paramMap.get('accountId')),
    );
  }
}
