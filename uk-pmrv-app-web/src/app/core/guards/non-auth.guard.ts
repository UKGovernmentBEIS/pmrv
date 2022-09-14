import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, switchMapTo } from 'rxjs';

import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class NonAuthGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly authService: AuthService) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMapTo(this.authService.isLoggedIn),
      map((isLoggedIn) => !isLoggedIn || this.router.parseUrl('')),
      first(),
    );
  }
}
