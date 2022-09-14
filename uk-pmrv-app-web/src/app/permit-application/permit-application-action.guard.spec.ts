import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, firstValueFrom, of } from 'rxjs';

import { RequestActionsService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { SharedModule } from '../shared/shared.module';
import { PermitApplicationActionGuard } from './permit-application-action.guard';
import { PermitApplicationStore } from './store/permit-application.store';

describe('PermitApplicationActionGuard', () => {
  let guard: PermitApplicationActionGuard;
  let store: PermitApplicationStore;

  const authService = {
    userStatus: new BehaviorSubject<UserStatusDTO>({ roleType: 'OPERATOR' }),
  };
  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    });
    guard = TestBed.inject(PermitApplicationActionGuard);
    store = TestBed.inject(PermitApplicationStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit', async () => {
    requestActionsService.getRequestActionByIdUsingGET.mockReturnValueOnce(
      of({ type: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED' } as any),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    requestActionsService.getRequestActionByIdUsingGET.mockReturnValueOnce(
      of({ type: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED' } as any),
    );
    const resetSpy = jest.spyOn(store, 'reset');

    await firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 })));

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestActionType).toEqual('PERMIT_ISSUANCE_APPLICATION_SUBMITTED');
    expect(store.getState().isRequestTask).toBeFalsy();
  });
});
