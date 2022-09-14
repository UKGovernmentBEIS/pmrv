import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, from, mapTo, Observable, of, switchMap, tap } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';
import { KeycloakLoginOptions, KeycloakProfile } from 'keycloak-js';

import {
  ApplicationUserDTO,
  AuthoritiesService,
  TermsAndConditionsService,
  TermsDTO,
  UsersService,
  UserStatusDTO,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly isLoggedIn = new BehaviorSubject<boolean>(null);
  readonly userStatus = new BehaviorSubject<UserStatusDTO>(null);
  readonly terms = new BehaviorSubject<TermsDTO>(null);
  readonly user = new BehaviorSubject<ApplicationUserDTO>(null);
  readonly userProfile = new BehaviorSubject<KeycloakProfile>(null);
  keycloakLoginOptions: KeycloakLoginOptions;

  constructor(
    private readonly keycloakService: KeycloakService,
    private readonly usersService: UsersService,
    private readonly authorityService: AuthoritiesService,
    private readonly termsAndConditionsService: TermsAndConditionsService,
    private readonly route: ActivatedRoute,
  ) {}

  login(options?: KeycloakLoginOptions): Promise<void> {
    let leaf = this.route.snapshot;

    while (leaf.firstChild) {
      leaf = leaf.firstChild;
    }

    return this.keycloakService.login({
      ...options,
      ...(leaf.data?.blockSignInRedirect ? { redirectUri: location.origin } : null),
    });
  }

  logout(redirectPath = ''): Promise<void> {
    return this.keycloakService.logout(location.origin + redirectPath);
  }

  loadUser(): Observable<ApplicationUserDTO> {
    return this.usersService.getCurrentUserUsingGET().pipe(tap((user) => this.user.next(user)));
  }

  loadUserStatus(): Observable<UserStatusDTO> {
    return this.authorityService
      .getCurrentUserStatusUsingGET()
      .pipe(tap((userStatus) => this.userStatus.next(userStatus)));
  }

  checkUser(): Observable<void> {
    return this.isLoggedIn.getValue() === null
      ? this.loadIsLoggedIn().pipe(
          switchMap((res: boolean) =>
            res
              ? combineLatest([this.loadUserStatus(), this.loadTerms(), this.loadUser(), this.loadUserProfile()]).pipe(
                  mapTo(undefined),
                )
              : of(undefined),
          ),
        )
      : of(undefined);
  }

  private loadUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloakService.loadUserProfile()).pipe(tap((res) => this.userProfile.next(res)));
  }

  private loadTerms(): Observable<TermsDTO> {
    return this.termsAndConditionsService.getLatestTermsUsingGET().pipe(tap((res) => this.terms.next(res)));
  }

  private loadIsLoggedIn(): Observable<boolean> {
    return from(this.keycloakService.isLoggedIn()).pipe(tap((isLoggedIn) => this.isLoggedIn.next(isLoggedIn)));
  }
}
