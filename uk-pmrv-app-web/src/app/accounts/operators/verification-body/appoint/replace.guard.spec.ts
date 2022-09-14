import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { AccountVerificationBodyService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../../error/testing/concurrency-error';
import { viewNotFoundOperatorError } from '../../errors/concurrency-error';
import { ReplaceGuard } from './replace.guard';

describe('ReplaceGuard', () => {
  let guard: ReplaceGuard;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const route = new ActivatedRouteSnapshotStub({ accountId: '1' });

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccountUsingGET: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [ConcurrencyTestingModule],
      providers: [{ provide: AccountVerificationBodyService, useValue: accountVerificationBodyService }],
    });
    guard = TestBed.inject(ReplaceGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if a verification body is found', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccountUsingGET.mockReturnValueOnce(
      of({ id: 1, name: 'testName' }),
    );

    await expect(lastValueFrom(guard.canActivate(route))).resolves.toBeTruthy();
  });

  it('should navigate to error page if a verification body is appointed', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccountUsingGET.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    await expect(lastValueFrom(guard.canActivate(route))).rejects.toBeTruthy();

    await expectConcurrentErrorToBe(viewNotFoundOperatorError(1));
  });

  it('should rethrow all other errors', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccountUsingGET.mockReturnValue(
      throwError(() => ({ status: 500 })),
    );

    await expect(lastValueFrom(guard.canActivate(route))).rejects.toBeTruthy();
  });
});
