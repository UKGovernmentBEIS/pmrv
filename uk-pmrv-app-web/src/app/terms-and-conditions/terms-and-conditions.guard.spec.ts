import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { ApplicationUserDTO, TermsDTO, UserStatusDTO } from 'pmrv-api';

import { mockClass, MockType } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { TermsAndConditionsGuard } from './terms-and-conditions.guard';

describe('TermsAndConditionsGuard', () => {
  let guard: TermsAndConditionsGuard;
  let router: Router;
  const keycloakService = mockClass(KeycloakService);

  const isLoggedIn$ = new BehaviorSubject<boolean>(null);
  const userStatus$ = new BehaviorSubject<UserStatusDTO>(null);
  const terms$ = new BehaviorSubject<TermsDTO>(null);
  const user$ = new BehaviorSubject<ApplicationUserDTO>(null);

  const authService: MockType<AuthService> = {
    isLoggedIn: isLoggedIn$,
    userStatus: userStatus$,
    terms: terms$,
    user: user$,
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: AuthService, useValue: authService },
      ],
    });
    guard = TestBed.inject(TermsAndConditionsGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to landing page if user is not logged in or terms match', async () => {
    isLoggedIn$.next(false);

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));

    isLoggedIn$.next(true);
    userStatus$.next({ loginStatus: 'DISABLED' });
    terms$.next({ version: 2, url: 'mock' });
    user$.next({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);

    terms$.next({ version: 1, url: 'mock' });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });
});
