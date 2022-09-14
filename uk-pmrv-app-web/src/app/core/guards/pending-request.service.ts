import { Injectable } from '@angular/core';

import { BehaviorSubject, defer, finalize, MonoTypeOperatorFunction, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PendingRequestService {
  private readonly trackedRequests = new Set<Observable<any>>();
  private readonly isRequestPending = new BehaviorSubject<boolean>(this.hasPendingRequests());
  readonly isRequestPending$ = this.isRequestPending.asObservable();

  hasPendingRequests(): boolean {
    return this.trackedRequests.size > 0;
  }

  trackRequest<T>(): MonoTypeOperatorFunction<T> {
    return (source: Observable<T>) =>
      defer(() => {
        this.trackedRequests.add(source);
        this.isRequestPending.next(this.hasPendingRequests());

        return source;
      }).pipe(
        finalize(() => {
          this.trackedRequests.delete(source);
          this.isRequestPending.next(this.hasPendingRequests());
        }),
      );
  }
}
