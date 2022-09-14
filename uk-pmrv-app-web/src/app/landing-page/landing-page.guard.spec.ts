import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { ApplicationUserDTO, TermsDTO, UserStatusDTO } from 'pmrv-api';

import { MockType } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { LandingPageGuard } from './landing-page.guard';

describe('LandingPageGuard', () => {
  let guard: LandingPageGuard;
  let router: Router;

  const authService: MockType<AuthService> = {
    isLoggedIn: new BehaviorSubject<boolean>(null),
    userStatus: new BehaviorSubject<UserStatusDTO>(null),
    terms: new BehaviorSubject<TermsDTO>(null),
    user: new BehaviorSubject<ApplicationUserDTO>(null),
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }, LandingPageGuard],
    });
    guard = TestBed.inject(LandingPageGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow if user is not logged in', () => {
    authService.isLoggedIn.next(false);

    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should allow if user is logged in and terms match and status is not ENABLED', () => {
    authService.isLoggedIn.next(true);
    authService.userStatus.next({ loginStatus: 'DISABLED' });
    authService.terms.next({ version: 1, url: 'asd' });
    authService.user.next({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });

    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should allow if user is logged in and no authority', () => {
    authService.userStatus.next({ loginStatus: 'NO_AUTHORITY' });

    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should redirect to dashboard if user is logged in and enabled and terms match', async () => {
    authService.userStatus.next({ loginStatus: 'ENABLED' });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('dashboard'));

    authService.terms.next({ version: 2, url: 'asd' });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('terms'));
  });

  it('should allow if user is logged in and adding another installation', async () => {
    authService.userStatus.next({ loginStatus: 'ENABLED' });
    authService.terms.next({ version: 1, url: 'asd' });
    authService.user.next({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });
    jest
      .spyOn(router, 'getCurrentNavigation')
      .mockReturnValue({ extras: { state: { addAnotherInstallation: true } } } as any);
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });
});
