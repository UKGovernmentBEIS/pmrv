import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, throwError } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData, RouterStubComponent } from '../../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { disabledVerificationBodyError, viewNotFoundVerificationBodyError } from '../../errors/concurrency-error';
import { AddGuard } from './add.guard';

describe('AddGuard', () => {
  let guard: AddGuard;

  const verificationBodiesService: Partial<jest.Mocked<VerificationBodiesService>> = {
    getVerificationBodyByIdUsingGET: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'verification-bodies/:verificationBodyId/disabled',
            component: RouterStubComponent,
          },
        ]),
        ConcurrencyTestingModule,
      ],
      declarations: [RouterStubComponent],
      providers: [{ provide: VerificationBodiesService, useValue: verificationBodiesService }],
    });
    guard = TestBed.inject(AddGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should block access if the body is disabled', async () => {
    verificationBodiesService.getVerificationBodyByIdUsingGET.mockReturnValue(asyncData({ status: 'DISABLED' }));
    const snapshot = new ActivatedRouteSnapshotStub({ verificationBodyId: '1' });

    await expect(lastValueFrom(guard.canActivate(snapshot))).resolves.toBeFalsy();

    await expectConcurrentErrorToBe(disabledVerificationBodyError);
  });

  it('should allow access if the body is active or pending', async () => {
    verificationBodiesService.getVerificationBodyByIdUsingGET.mockReturnValue(asyncData({ status: 'ACTIVE' }));
    const snapshot = new ActivatedRouteSnapshotStub({ verificationBodyId: '1' });

    await expect(lastValueFrom(guard.canActivate(snapshot))).resolves.toBeTruthy();

    verificationBodiesService.getVerificationBodyByIdUsingGET.mockReturnValue(asyncData({ status: 'PENDING' }));

    await expect(lastValueFrom(guard.canActivate(snapshot))).resolves.toBeTruthy();
  });

  it('should display the error page if the verification body is not found', async () => {
    verificationBodiesService.getVerificationBodyByIdUsingGET.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: HttpStatusCode.NotFound })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ verificationBodyId: '1' }))),
    ).rejects.toBeTruthy();

    await expectConcurrentErrorToBe(viewNotFoundVerificationBodyError);
  });
});
