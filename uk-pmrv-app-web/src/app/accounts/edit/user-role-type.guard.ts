import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, pluck } from 'rxjs';

import { AuthService } from '../../core/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class UserRoleTypeGuard implements CanActivate {
  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.userStatus.pipe(
      first(),
      pluck('roleType'),
      map((roleType) => roleType === 'REGULATOR' || this.router.parseUrl('/dashboard')),
    );
  }
}
