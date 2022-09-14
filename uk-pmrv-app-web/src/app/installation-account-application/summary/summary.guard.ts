import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { Section } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: InstallationAccountApplicationStore, private readonly router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.select('tasks').pipe(
      map(
        (sections: Section[]) => !sections.some((section) => section.status !== 'complete') || this.router.parseUrl(''),
      ),
      first(),
    );
  }
}
