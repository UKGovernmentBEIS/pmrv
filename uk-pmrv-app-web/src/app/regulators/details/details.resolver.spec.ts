import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { BehaviorSubject, lastValueFrom, throwError } from 'rxjs';

import { RegulatorUserDTO, RegulatorUsersService, UsersService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { viewNotFoundRegulatorError } from '../errors/concurrency-error';
import { DetailsResolver } from './details.resolver';

describe('DetailsResolver', () => {
  let resolver: DetailsResolver;
  let regulatorUsersService: Partial<jest.Mocked<RegulatorUsersService>>;
  let usersService: Partial<jest.Mocked<UsersService>>;

  const user: RegulatorUserDTO = {
    email: 'test@host.com',
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'developer',
    phoneNumber: '23456',
  };

  beforeEach(() => {
    regulatorUsersService = { getRegulatorUserByCaAndIdUsingGET: jest.fn().mockReturnValue(asyncData(user)) };
    usersService = { getCurrentUserUsingGET: jest.fn().mockReturnValue(asyncData(user)) };

    const authService = {
      userStatus: new BehaviorSubject<UserStatusDTO>({ loginStatus: 'ENABLED', userId: 'ABC1', roleType: 'REGULATOR' }),
    };

    TestBed.configureTestingModule({
      imports: [ConcurrencyTestingModule],
      providers: [
        { provide: RegulatorUsersService, useValue: regulatorUsersService },
        { provide: UsersService, useValue: usersService },
        { provide: AuthService, useValue: authService },
      ],
    });
    resolver = TestBed.inject(DetailsResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should provide the information of another user', async () => {
    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ userId: '1234567' }))),
    ).resolves.toEqual(user);

    expect(regulatorUsersService.getRegulatorUserByCaAndIdUsingGET).toHaveBeenCalledWith('1234567');
  });

  it('should provide current user information', async () => {
    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).resolves.toEqual(user);

    expect(usersService.getCurrentUserUsingGET).toHaveBeenCalled();
  });

  it('should return to regulator list when visiting a deleted user', async () => {
    usersService.getCurrentUserUsingGET.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: HttpStatusCode.BadRequest,
            error: { code: 'AUTHORITY1003', message: 'User is not related to competent authority', data: [] },
          }),
      ),
    );

    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).rejects.toBeTruthy();
    await expectConcurrentErrorToBe(viewNotFoundRegulatorError);
  });
});
