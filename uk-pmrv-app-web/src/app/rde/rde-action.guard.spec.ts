import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { RdeActionGuard } from './rde-action.guard';
import { RdeStore } from './store/rde.store';

describe('RdeActionGuard', () => {
  let guard: RdeActionGuard;
  let store: RdeStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(RdeActionGuard);
    store = TestBed.inject(RdeStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow Rde', async () => {
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
