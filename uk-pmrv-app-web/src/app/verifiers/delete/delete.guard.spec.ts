import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { BehaviorSubject, lastValueFrom, throwError } from 'rxjs';

import { UsersService, UserStatusDTO, VerifierUsersService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { ErrorCode } from '../../error/business-errors';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { saveNotFoundVerifierError } from '../errors/concurrency-error';
import { DeleteGuard } from './delete.guard';

describe('DeleteGuard', () => {
  let guard: DeleteGuard;

  const response = {
    user: { id: '1', firstName: 'Bob', lastName: 'Squarepants', email: 'bob@squarepants.com', phoneNumber: '123312' },
  };

  let usersService: Partial<jest.Mocked<UsersService>>;
  let verifierUsersService: Partial<jest.Mocked<VerifierUsersService>>;

  const userStatus$ = new BehaviorSubject<UserStatusDTO>({
    loginStatus: 'ENABLED',
    roleType: 'VERIFIER',
    userId: '2',
  });
  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: userStatus$,
  };

  beforeEach(() => {
    usersService = {
      getCurrentUserUsingGET: jest.fn().mockReturnValue(asyncData(response)),
    };
    verifierUsersService = {
      getVerifierUserByIdUsingGET: jest.fn().mockReturnValue(asyncData(response)),
    };

    TestBed.configureTestingModule({
      imports: [ConcurrencyTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: UsersService, useValue: usersService },
        { provide: VerifierUsersService, useValue: verifierUsersService },
      ],
    });

    guard = TestBed.inject(DeleteGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow if verifier exists', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' }))),
    ).resolves.toBeTruthy();

    expect(verifierUsersService.getVerifierUserByIdUsingGET).toHaveBeenCalledWith('1');
  });

  it('should allow if verifier is the current user', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '2' }))),
    ).resolves.toBeTruthy();

    expect(usersService.getCurrentUserUsingGET).toHaveBeenCalled();
  });

  it('should block access if current user is not a verifier', async () => {
    usersService.getCurrentUserUsingGET.mockReturnValue(throwError(() => 'User not found'));

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '2' }))),
    ).rejects.toBeTruthy();
  });

  it('should block access if verifier is not found', async () => {
    verifierUsersService.getVerifierUserByIdUsingGET.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: HttpStatusCode.BadRequest,
            error: { code: ErrorCode.AUTHORITY1006 },
          }),
      ),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' }))),
    ).rejects.toBeTruthy();

    await expectConcurrentErrorToBe(saveNotFoundVerifierError);
  });

  it('should resolve the user', async () => {
    await lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' })));

    expect(guard.resolve()).toEqual(response);
  });
});
