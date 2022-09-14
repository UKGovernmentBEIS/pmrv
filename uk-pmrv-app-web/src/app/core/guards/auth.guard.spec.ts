import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { ApplicationUserDTO, TermsDTO, UserStatusDTO } from 'pmrv-api';

import { MockType } from '../../../testing';
import { AuthService } from '../services/auth.service';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;
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
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    });
    guard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it("should redirect to terms if user is logged in and terms don't match", async () => {
    authService.isLoggedIn.next(true);
    authService.userStatus.next({ loginStatus: 'DISABLED' });
    authService.terms.next({ version: 2, url: 'asd' });
    authService.user.next({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('terms'));

    authService.user.next({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 2 });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));

    authService.userStatus.next({ loginStatus: 'ENABLED' });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should redirect to landing page if user is not logged in or is disabled', async () => {
    authService.isLoggedIn.next(false);

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));

    authService.isLoggedIn.next(true);
    authService.userStatus.next({ loginStatus: 'DISABLED' });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));

    authService.isLoggedIn.next(true);
    authService.userStatus.next({ loginStatus: 'TEMP_DISABLED' });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });
});
