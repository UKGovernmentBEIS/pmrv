import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, Resolve, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMapTo } from 'rxjs';

import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate, CanActivateChild, Resolve<void> {
  constructor(protected router: Router, protected authService: AuthService) {}

  canActivate(): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMapTo(
        combineLatest([
          this.authService.isLoggedIn,
          this.authService.userStatus,
          this.authService.terms,
          this.authService.user,
        ]),
      ),
      map(([isLoggedIn, userStatus, terms, user]) =>
        isLoggedIn &&
        terms.version === user.termsVersion &&
        !['DISABLED', 'TEMP_DISABLED'].includes(userStatus.loginStatus)
          ? (true as const)
          : isLoggedIn && terms.version !== user.termsVersion
          ? this.router.parseUrl('/terms')
          : this.router.parseUrl(''),
      ),
      first(),
    );
  }

  canActivateChild(): Observable<true | UrlTree> {
    return this.canActivate();
  }

  resolve(): Observable<void> {
    return this.authService.checkUser();
  }
}
