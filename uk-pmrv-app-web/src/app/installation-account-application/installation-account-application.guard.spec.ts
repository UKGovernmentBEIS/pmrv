import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { ApplicationUserDTO, TermsDTO, UserStatusDTO } from 'pmrv-api';

import { MockType } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { InstallationAccountApplicationGuard } from './installation-account-application.guard';

describe('InstallationAccountApplicationGuard', () => {
  let guard: InstallationAccountApplicationGuard;
  let router: Router;

  const authService: MockType<AuthService> = {
    isLoggedIn: new BehaviorSubject<boolean>(null),
    userStatus: new BehaviorSubject<UserStatusDTO>(null),
    terms: new BehaviorSubject<TermsDTO>(null),
    user: new BehaviorSubject<ApplicationUserDTO>(null),
    checkUser: jest.fn(() => of(undefined)),
  };

  const setUser = (roleType: UserStatusDTO['roleType'], loginStatus?: UserStatusDTO['loginStatus']) => {
    authService.userStatus.next({ roleType, loginStatus });
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }, InstallationAccountApplicationGuard],
    });
    guard = TestBed.inject(InstallationAccountApplicationGuard);
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
  });

  it('should redirect to landing page if user is not logged in', async () => {
    authService.isLoggedIn.next(false);

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should redirect to landing page if user is regulator, logged in and not enabled', async () => {
    authService.isLoggedIn.next(true);
    setUser('REGULATOR', 'DISABLED');

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should allow disabled operator or operator with no authority', async () => {
    authService.isLoggedIn.next(true);
    setUser('OPERATOR', 'DISABLED');

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();

    authService.isLoggedIn.next(true);
    setUser('OPERATOR', 'NO_AUTHORITY');

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });

  it('should redirect to landing page if user is verifier', async () => {
    authService.isLoggedIn.next(true);
    setUser('VERIFIER', 'ENABLED');

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });
});
