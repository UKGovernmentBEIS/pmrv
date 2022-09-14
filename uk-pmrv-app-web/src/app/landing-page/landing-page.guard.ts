import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMapTo } from 'rxjs';

import { AuthService } from '../core/services/auth.service';

@Injectable()
export class LandingPageGuard implements CanActivate {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
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
        !isLoggedIn
          ? true
          : terms.version !== user.termsVersion
          ? this.router.parseUrl('/terms')
          : userStatus.loginStatus === 'ENABLED' &&
            !this.router.getCurrentNavigation()?.extras.state?.addAnotherInstallation
          ? this.router.parseUrl('dashboard')
          : true,
      ),
      first(),
    );
  }
}
