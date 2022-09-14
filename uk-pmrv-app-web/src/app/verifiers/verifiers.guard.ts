import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { UsersAuthoritiesInfoDTO, VerifierAuthoritiesService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class VerifiersGuard implements Resolve<UsersAuthoritiesInfoDTO> {
  constructor(private readonly verifierAuthoritiesService: VerifierAuthoritiesService) {}

  resolve(): Observable<UsersAuthoritiesInfoDTO> {
    return this.verifierAuthoritiesService.getVerifierAuthoritiesUsingGET();
  }
}
