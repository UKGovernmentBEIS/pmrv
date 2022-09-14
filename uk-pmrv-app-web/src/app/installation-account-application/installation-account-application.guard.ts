import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMapTo } from 'rxjs';

import { AuthService } from '../core/services/auth.service';

@Injectable()
export class InstallationAccountApplicationGuard implements CanActivate {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}

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
        (userStatus.roleType === 'OPERATOR' ||
          (userStatus.roleType === 'REGULATOR' && userStatus.loginStatus === 'ENABLED'))
          ? (true as const)
          : this.router.parseUrl(isLoggedIn && terms.version !== user.termsVersion ? '/terms' : ''),
      ),
      first(),
    );
  }
}
