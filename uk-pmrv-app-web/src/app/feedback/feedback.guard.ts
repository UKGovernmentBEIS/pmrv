import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '../core/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class FeedbackGuard implements CanActivate {
  constructor(protected router: Router, protected authService: AuthService) {}

  canActivate(): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() => this.authService.isLoggedIn),
      map((isLoggedIn) => isLoggedIn || this.router.parseUrl('')),
      first(),
    );
  }
}
