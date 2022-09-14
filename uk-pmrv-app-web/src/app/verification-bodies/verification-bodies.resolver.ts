import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import { Observable } from 'rxjs';

import { VerificationBodiesService, VerificationBodyInfoResponseDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class VerificationBodiesResolver implements Resolve<Observable<VerificationBodyInfoResponseDTO>> {
  constructor(private readonly verificationBodiesService: VerificationBodiesService) {}

  resolve(): Observable<VerificationBodyInfoResponseDTO> {
    return this.verificationBodiesService.getVerificationBodiesUsingGET();
  }
}
