import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { AccountDetailsDTO, AccountViewService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../../testing';
import { mockedAccount } from '../../testing/mock-data';
import { AccountStatusGuard } from './account-status.guard';

describe('AddUserGuard', () => {
  let guard: AccountStatusGuard;
  let router: Router;
  let accountViewService: Partial<jest.Mocked<AccountViewService>>;

  beforeEach(() => {
    accountViewService = {
      getAccountByIdUsingGET: jest.fn().mockReturnValue(asyncData<AccountDetailsDTO>(mockedAccount)),
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AccountViewService, useValue: accountViewService }],
    });
    guard = TestBed.inject(AccountStatusGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be activated', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccount.id }))),
    ).resolves.toBeTruthy();

    expect(accountViewService.getAccountByIdUsingGET).toHaveBeenCalledWith(mockedAccount.id);
  });

  it('should not be activated', async () => {
    const unapprovedAccount: AccountDetailsDTO = { ...mockedAccount, status: 'UNAPPROVED' };
    accountViewService.getAccountByIdUsingGET.mockReturnValue(asyncData<AccountDetailsDTO>(unapprovedAccount));

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccount.id }))),
    ).resolves.toEqual(router.parseUrl(`/accounts/${mockedAccount.id}`));

    expect(accountViewService.getAccountByIdUsingGET).toHaveBeenCalledWith(mockedAccount.id);

    const deniedAccount: AccountDetailsDTO = { ...mockedAccount, status: 'DENIED' };
    accountViewService.getAccountByIdUsingGET.mockReturnValue(asyncData<AccountDetailsDTO>(deniedAccount));

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccount.id }))),
    ).resolves.toEqual(router.parseUrl(`/accounts/${mockedAccount.id}`));

    expect(accountViewService.getAccountByIdUsingGET).toHaveBeenCalledWith(mockedAccount.id);
  });
});
