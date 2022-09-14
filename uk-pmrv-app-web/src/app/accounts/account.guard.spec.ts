import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AccountViewService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass, MockType } from '../../testing';
import { AccountGuard } from './account.guard';
import { mockedAccount } from './testing/mock-data';

describe('AccountGuard', () => {
  let guard: AccountGuard;
  let accountViewService: MockType<AccountViewService>;

  beforeEach(() => {
    accountViewService = mockClass(AccountViewService);
    accountViewService.getAccountByIdUsingGET.mockReturnValueOnce(of(mockedAccount));

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [AccountGuard, { provide: AccountViewService, useValue: accountViewService }],
    });
    guard = TestBed.inject(AccountGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return account details', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1' }))),
    ).resolves.toBeTruthy();

    expect(accountViewService.getAccountByIdUsingGET).toHaveBeenCalledWith(1);

    await expect(guard.resolve(new ActivatedRouteSnapshotStub({ accountId: '1' }))).toEqual(mockedAccount);
  });
});
