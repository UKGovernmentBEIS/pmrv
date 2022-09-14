import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, throwError } from 'rxjs';

import { OperatorUserDTO, OperatorUsersService, UsersService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, address, asyncData } from '../../../../testing';
import { AuthService } from '../../../core/services/auth.service';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { DetailsGuard } from '../details/details.guard';
import { saveNotFoundOperatorError } from '../errors/concurrency-error';
import { DeleteGuard } from './delete.guard';

describe('DeleteGuard', () => {
  let guard: DeleteGuard;
  let usersService: Partial<jest.Mocked<UsersService>>;
  let operatorUsersService: Partial<jest.Mocked<OperatorUsersService>>;

  const operator: OperatorUserDTO = {
    address,
    email: 'test@host.com',
    firstName: 'Mary',
    lastName: 'Za',
    mobileNumber: { countryCode: '+30', number: '1234567890' },
    phoneNumber: { countryCode: '+30', number: '123456780' },
  };

  beforeEach(() => {
    operatorUsersService = {
      getOperatorUserByIdUsingGET: jest.fn().mockReturnValue(asyncData<OperatorUserDTO>(operator)),
    };
    usersService = {
      getCurrentUserUsingGET: jest.fn().mockReturnValue(asyncData<OperatorUserDTO>(operator)),
    };
    const authService: Partial<jest.Mocked<AuthService>> = {
      userStatus: new BehaviorSubject<UserStatusDTO>({ loginStatus: 'ENABLED', userId: 'ABC1', roleType: 'OPERATOR' }),
    };
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ConcurrencyTestingModule],
      providers: [
        DetailsGuard,
        { provide: UsersService, useValue: usersService },
        { provide: AuthService, useValue: authService },
        { provide: OperatorUsersService, useValue: operatorUsersService },
      ],
    });
    guard = TestBed.inject(DeleteGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should provide other user information', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'asdf4' }))),
    ).resolves.toBeTruthy();

    await expect(guard.resolve()).toEqual(operator);

    expect(operatorUsersService.getOperatorUserByIdUsingGET).toHaveBeenCalledWith(1, 'asdf4');
  });

  it('should provide current user information', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).resolves.toBeTruthy();

    await expect(guard.resolve()).toEqual(operator);

    expect(usersService.getCurrentUserUsingGET).toHaveBeenCalled();
  });

  it('should throw an error when visiting a deleted user', async () => {
    operatorUsersService.getOperatorUserByIdUsingGET.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1004' } })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'asdf4' }))),
    ).rejects.toBeTruthy();

    await expectConcurrentErrorToBe(saveNotFoundOperatorError(1));
  });
});
