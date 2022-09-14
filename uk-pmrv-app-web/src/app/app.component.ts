import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';

import { combineLatest, filter, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { ScrollService } from 'govuk-components';

import { AuthService } from './core/services/auth.service';
import { DestroySubject } from './core/services/destroy-subject.service';
import { DocumentEventService } from './shared/services/document-event.service';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [DestroySubject],
})
export class AppComponent implements OnInit {
  readonly isPermissionsLoaded$: Observable<
    | false
    | { showRegulators: boolean; showDashboard: boolean; showVerifiers: boolean; showAuthorizedOperators: boolean }
  > = this.authService.isLoggedIn.pipe(
    switchMap((isLoggedIn) =>
      isLoggedIn
        ? combineLatest([
            this.authService.userStatus.pipe(
              map((userStatus) => ['DISABLED', 'TEMP_DISABLED'].includes(userStatus?.loginStatus)),
            ),
            this.authService.userStatus.pipe(map((userStatus) => userStatus?.roleType === 'REGULATOR')),
            this.authService.userStatus.pipe(map((userStatus) => userStatus?.roleType === 'VERIFIER')),
            this.authService.userStatus.pipe(
              map(
                (userStatus) =>
                  userStatus !== null &&
                  (userStatus?.roleType !== 'OPERATOR' || userStatus?.loginStatus !== 'NO_AUTHORITY'),
              ),
            ),
            this.authService.userStatus.pipe(
              map(
                (userStatus) =>
                  userStatus !== null &&
                  userStatus?.roleType === 'OPERATOR' &&
                  userStatus?.loginStatus !== 'NO_AUTHORITY',
              ),
            ),
          ]).pipe(
            map(
              ([isDisabled, showRegulators, showVerifiers, showDashboard, showAuthorizedOperators]) =>
                !isDisabled &&
                Object.values({ showRegulators, showVerifiers, showDashboard, showAuthorizedOperators }).some(
                  (permission) => !!permission,
                ) && {
                  showRegulators,
                  showVerifiers,
                  showDashboard,
                  showAuthorizedOperators,
                },
            ),
          )
        : of(false as const),
    ),
  );

  constructor(
    public readonly authService: AuthService,
    private readonly _scroll: ScrollService,
    private readonly _documentEvent: DocumentEventService,
    private readonly titleService: Title,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    const appTitle = this.titleService.getTitle();

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let child = this.route.firstChild;
          while (child.firstChild) {
            child = child.firstChild;
          }
          if (child.snapshot.data['pageTitle']) {
            return child.snapshot.data['pageTitle'];
          }
          return appTitle;
        }),
        takeUntil(this.destroy$),
      )
      .subscribe((title: string) => this.titleService.setTitle(title));
  }
}
