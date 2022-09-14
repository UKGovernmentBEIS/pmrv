import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { RfiActionGuard } from './rfi-action.guard';
import { RfiStore } from './store/rfi.store';

describe('RfiActionGuard', () => {
  let guard: RfiActionGuard;
  let store: RfiStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(RfiActionGuard);
    store = TestBed.inject(RfiStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit', async () => {
    requestActionsService.getRequestActionByIdUsingGET.mockReturnValueOnce(of({ id: 1, payload: {} } as any));

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 1 }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    requestActionsService.getRequestActionByIdUsingGET.mockReturnValueOnce(of({ id: 1, payload: {} } as any));
    const resetSpy = jest.spyOn(store, 'reset');

    await firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 1 })));

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().actionId).toEqual(1);
  });
});
