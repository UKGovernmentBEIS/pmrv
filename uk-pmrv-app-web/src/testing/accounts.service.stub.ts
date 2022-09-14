import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';

import { AccountsService } from 'pmrv-api';

@Injectable()
export class AccountsServiceStub implements Partial<AccountsService> {
  isExistingAccountNameUsingGET(name: string): Observable<any> {
    if (name === 'Existing') {
      return of(true);
    } else {
      return of(false);
    }
  }
}
