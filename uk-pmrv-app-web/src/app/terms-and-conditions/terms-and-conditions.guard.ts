import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '../core/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class TermsAndConditionsGuard implements CanActivate {
  constructor(protected router: Router, protected authService: AuthService) {}

  canActivate(): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() => combineLatest([this.authService.isLoggedIn, this.authService.terms, this.authService.user])),
      map(
        ([isLoggedIn, terms, user]) => (isLoggedIn && terms.version !== user.termsVersion) || this.router.parseUrl(''),
      ),
      first(),
    );
  }
}
