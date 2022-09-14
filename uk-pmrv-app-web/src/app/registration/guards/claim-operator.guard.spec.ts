import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, map, throwError } from 'rxjs';

import { OperatorInvitedUserInfoDTO, OperatorUsersRegistrationService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../testing';
import { UserRegistrationStore } from '../store/user-registration.store';
import { ClaimOperatorGuard } from './claim-operator.guard';

describe('ClaimOperatorGuard', () => {
  let guard: ClaimOperatorGuard;
  let operatorUsersRegistrationService: Partial<jest.Mocked<OperatorUsersRegistrationService>>;

  const user: OperatorInvitedUserInfoDTO = { firstName: 'Boy', lastName: 'Cott', email: 'test@test.gr' };

  beforeEach(() => {
    operatorUsersRegistrationService = {
      acceptOperatorInvitationUsingPOST: jest.fn(),
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: OperatorUsersRegistrationService, useValue: operatorUsersRegistrationService }],
    });

    guard = TestBed.inject(ClaimOperatorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should resolve the installation name', async () => {
    const resolvedData = { accountInstallationName: 'My Account', roleCode: 'operator' };
    operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST.mockReturnValue(
      asyncData({ invitationStatus: 'ACCEPTED', ...resolvedData }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: 'email-token' }))),
    ).resolves.toBeTruthy();

    expect(guard.resolve()).toEqual(resolvedData);
  });

  it('should navigate to dashboard if there is no token query param', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub()).pipe(map((url) => url.toString()))),
    ).resolves.toEqual('/');
    expect(operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST).not.toHaveBeenCalled();
  });

  it('should navigate to invalid link when the link has expired', async () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EMAIL1001' }, status: 400 })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: 'email-token' }))),
    ).resolves.toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['/registration/invitation/invalid-link'], {
      queryParams: { code: 'EMAIL1001' },
    });
  });

  it('should navigate to invalid link when the link is invalid', async () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'TOKEN1001' }, status: 400 })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: 'email-token' }))),
    ).resolves.toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['/registration/invitation/invalid-link'], {
      queryParams: { code: 'TOKEN1001' },
    });
  });

  it('should navigate to contact details for new users', async () => {
    const token = 'email-token';
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST.mockReturnValue(
      asyncData({
        invitationStatus: 'PENDING_USER_REGISTRATION',
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(null, { token }))),
    ).resolves.toBeFalsy();

    expect(TestBed.inject(UserRegistrationStore).getState()).toEqual({
      email: user.email,
      token,
      isInvited: true,
      isSummarized: false,
      password: null,
      userRegistrationDTO: user,
      invitationStatus: 'PENDING_USER_REGISTRATION',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/registration/user/contact-details']);
  });

  it('should navigate to choose password when an emitter is invited to join as an operator', async () => {
    const token = 'email-token';
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST.mockReturnValue(
      asyncData({
        invitationStatus: 'PENDING_USER_ENABLE',
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(null, { token }))),
    ).resolves.toBeFalsy();

    expect(TestBed.inject(UserRegistrationStore).getState()).toEqual({
      email: user.email,
      token,
      isInvited: true,
      isSummarized: false,
      password: null,
      userRegistrationDTO: user,
      invitationStatus: 'PENDING_USER_ENABLE',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/registration/user/choose-password']);
  });
});
