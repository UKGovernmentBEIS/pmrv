import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, first } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import {
  ApplicationUserDTO,
  AuthoritiesService,
  TermsAndConditionsService,
  TermsDTO,
  UsersService,
  UserStatusDTO,
} from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let activatedRoute: ActivatedRoute;

  const keycloakService = mockClass(KeycloakService);

  const user$ = new BehaviorSubject<ApplicationUserDTO>({
    email: 'test@test.com',
    firstName: 'test',
    lastName: 'test',
    termsVersion: 1,
  });
  const usersService: Partial<jest.Mocked<UsersService>> = {
    getCurrentUserUsingGET: jest.fn().mockReturnValue(user$),
  };

  const userStatus$ = new BehaviorSubject<UserStatusDTO>({
    loginStatus: 'ENABLED',
    roleType: 'OPERATOR',
    userId: 'opTestId',
  });
  const authoritiesService: Partial<jest.Mocked<AuthoritiesService>> = {
    getCurrentUserStatusUsingGET: jest.fn().mockReturnValue(userStatus$),
  };

  const terms$ = new BehaviorSubject<TermsDTO>({ url: '/test', version: 1 });
  const termsService: Partial<jest.Mocked<TermsAndConditionsService>> = {
    getLatestTermsUsingGET: jest.fn().mockReturnValue(terms$),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: UsersService, useValue: usersService },
        { provide: AuthoritiesService, useValue: authoritiesService },
        { provide: TermsAndConditionsService, useValue: termsService },
      ],
    });
    service = TestBed.inject(AuthService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    keycloakService.loadUserProfile.mockResolvedValue({ email: 'test@test.com' });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login', async () => {
    await service.login();
    await service.loadUser();

    expect(keycloakService.login).toHaveBeenCalledTimes(1);
    expect(keycloakService.login).toHaveBeenCalledWith({});
    expect(usersService.getCurrentUserUsingGET).toHaveBeenCalledTimes(1);
  });

  it('should logout', async () => {
    await service.logout();

    expect(keycloakService.logout).toHaveBeenCalled();
  });

  it('should load and update user status', async () => {
    expect(service.userStatus.getValue()).toBeNull();

    service
      .loadUserStatus()
      .pipe(first())
      .subscribe((res) => expect(res).toEqual(userStatus$.getValue()));
    expect(service.userStatus.getValue()).toEqual(userStatus$.getValue());
  });

  it('should update all user info when checkUser is called', () => {
    service.isLoggedIn.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.userStatus.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.terms.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.user.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.userProfile.pipe(first()).subscribe((res) => expect(res).toBeNull());

    keycloakService.isLoggedIn.mockResolvedValueOnce(false);

    service
      .checkUser()
      .pipe(first())
      .subscribe((res) => expect(res).toBeUndefined());
    service.isLoggedIn.pipe(first()).subscribe((res) => expect(res).toBeFalsy());
    service.userStatus.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.terms.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.user.pipe(first()).subscribe((res) => expect(res).toBeNull());
    service.userProfile.pipe(first()).subscribe((res) => expect(res).toBeNull());

    service.isLoggedIn.next(null);
    keycloakService.isLoggedIn.mockResolvedValueOnce(true);

    service
      .checkUser()
      .pipe(first())
      .subscribe((res) => expect(res).toBeUndefined());
    service.isLoggedIn.pipe(first()).subscribe((res) => expect(res).toBeTruthy());
    service.userStatus.pipe(first()).subscribe((res) => expect(res).toEqual(userStatus$.getValue()));
    service.terms.pipe(first()).subscribe((res) => expect(res).toEqual(terms$.getValue()));
    service.user.pipe(first()).subscribe((res) => expect(res).toEqual(user$.getValue()));
    service.userProfile.pipe(first()).subscribe((res) => expect(res).toEqual({ email: 'test@test.com' }));
  });

  it('should not update user info if logged in is already determined', () => {
    service.isLoggedIn.next(false);
    const spy = jest.spyOn(service, 'loadUserStatus');

    service
      .checkUser()
      .pipe(first())
      .subscribe((res) => expect(res).toBeUndefined());
    expect(spy).not.toHaveBeenCalled();
  });

  it('should redirect to origin if leaf data is blocking sign in redirect', async () => {
    activatedRoute.snapshot = new ActivatedRouteSnapshotStub(null, null, { blockSignInRedirect: true });

    await service.login();

    expect(keycloakService.login).toHaveBeenCalledTimes(1);
    expect(keycloakService.login).toHaveBeenCalledWith({ redirectUri: location.origin });
  });
});
