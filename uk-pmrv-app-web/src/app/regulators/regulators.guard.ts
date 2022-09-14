import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { RegulatorAuthoritiesService,RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class RegulatorsGuard implements Resolve<RegulatorUsersAuthoritiesInfoDTO> {
  constructor(private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService) {}

  resolve(): Observable<RegulatorUsersAuthoritiesInfoDTO> {
    return this.regulatorAuthoritiesService.getCaRegulatorsUsingGET();
  }
}
