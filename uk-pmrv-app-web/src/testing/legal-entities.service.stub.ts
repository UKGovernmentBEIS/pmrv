import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';

import { LegalEntitiesService } from 'pmrv-api';

@Injectable()
export class LegalEntitiesServiceStub implements Partial<LegalEntitiesService> {
  isExistingLegalEntityNameUsingGET(name: string): Observable<boolean>;
  isExistingLegalEntityNameUsingGET(
    name: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: '*/*';
    },
  ): never;
  isExistingLegalEntityNameUsingGET(
    name: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: '*/*';
    },
  ): never;
  isExistingLegalEntityNameUsingGET(
    name: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: {
      httpHeaderAccept?: '*/*';
    },
  ): never;
  isExistingLegalEntityNameUsingGET(name: string): Observable<boolean> {
    if (name === 'Mock Entity') {
      return of(true);
    } else {
      return of(false);
    }
  }
}
