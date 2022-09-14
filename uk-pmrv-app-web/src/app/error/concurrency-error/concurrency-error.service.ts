import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject, from, ignoreElements, Observable } from 'rxjs';

import { ConcurrencyError } from './concurrency-error';

@Injectable({ providedIn: 'root' })
export class ConcurrencyErrorService {
  private readonly errorSubject = new BehaviorSubject<ConcurrencyError>(null);
  readonly error$ = this.errorSubject.asObservable();

  constructor(private readonly router: Router) {}

  showError(error: ConcurrencyError): Observable<boolean> {
    this.errorSubject.next(error);

    return from(this.router.navigate(['/error/concurrency'], { skipLocationChange: true })).pipe(ignoreElements());
  }

  // this method is used to bypass the PendingRequestGuard when the error has to be caught before the
  // PendingRequestService tracks the request
  showErrorForceNavigation(error: ConcurrencyError): Observable<boolean> {
    this.errorSubject.next(error);

    return from(
      this.router.navigate(['/error/concurrency'], { state: { forceNavigation: true }, skipLocationChange: true }),
    ).pipe(ignoreElements());
  }

  clear(): void {
    this.errorSubject.next(null);
  }
}
