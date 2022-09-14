import { Injectable } from '@angular/core';
import { CanDeactivate, Router } from '@angular/router';

import { combineLatest, first, map, Observable, tap } from 'rxjs';

import { PendingRequest } from '../interfaces/pending-request.interface';
import { PendingRequestService } from './pending-request.service';

@Injectable({ providedIn: 'root' })
export class PendingRequestGuard implements CanDeactivate<PendingRequest> {
  constructor(private readonly router: Router, private readonly pendingRequest: PendingRequestService) {}

  canDeactivate(component: PendingRequest | any): boolean | Observable<boolean> {
    return (
      this.router.getCurrentNavigation()?.extras?.state?.forceNavigation ||
      combineLatest([
        this.pendingRequest.isRequestPending$,
        ...(this.isPendingRequest(component) ? [component.pendingRequest.isRequestPending$] : []),
      ]).pipe(
        map((pendingRequests) => pendingRequests.some((isRequestPending) => isRequestPending)),
        first(),
        tap((isPending) => {
          if (isPending) {
            alert(
              'A server request is pending. We suggest that you stay on this page in order not to lose your progress.',
            );
          }
        }),
        map((isPending) => !isPending),
      )
    );
  }

  private isPendingRequest(component: PendingRequest | any): component is PendingRequest {
    return component.pendingRequest;
  }
}
