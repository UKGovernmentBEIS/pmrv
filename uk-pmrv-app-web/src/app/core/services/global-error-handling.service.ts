import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorHandler, Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';

import { EMPTY, first, from, Observable, switchMap, switchMapTo, throwError } from 'rxjs';

import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class GlobalErrorHandlingService implements ErrorHandler {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly ngZone: NgZone,
  ) {}

  handleError(error: unknown): void {
    this.ngZone.run(() =>
      error instanceof HttpErrorResponse && error.status === HttpStatusCode.NotFound
        ? this.router.navigate(['/error', '404'], { state: { forceNavigation: true } })
        : this.router.navigate(['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true }),
    );
    console.error('ERROR', error);
  }

  handleHttpError(res: HttpErrorResponse): Observable<never> {
    switch (res.status) {
      case HttpStatusCode.InternalServerError:
        return from(
          this.router.navigate(['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true }),
        ).pipe(switchMapTo(EMPTY));
      case HttpStatusCode.Unauthorized:
        return from(this.authService.login()).pipe(switchMapTo(EMPTY));
      case HttpStatusCode.Forbidden:
        return this.authService.loadUserStatus().pipe(
          first(),
          switchMap((userStatus) =>
            userStatus.loginStatus === 'DELETED'
              ? from(this.authService.logout())
              : from(this.router.navigate([''], { state: { forceNavigation: true } })),
          ),
          switchMapTo(EMPTY),
        );
      default:
        return throwError(() => res);
    }
  }
}
