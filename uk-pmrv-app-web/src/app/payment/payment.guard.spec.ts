import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { SharedStore } from '@shared/store/shared.store';

import { RequestTaskItemDTO, TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { PaymentGuard } from './payment.guard';
import { initialState } from './store/payment.state';
import { PaymentStore } from './store/payment.store';
describe('PaymentGuard', () => {
  let guard: PaymentGuard;
  let store: PaymentStore;
  let sharedStore: SharedStore;

  const tasksService = mockClass(TasksService);
  const mockRequestTaskItem: RequestTaskItemDTO = {
    requestTask: {
      id: 1,
      type: 'PERMIT_ISSUANCE_MAKE_PAYMENT',
    },
    requestInfo: {
      id: '2',
      type: 'PERMIT_ISSUANCE',
      accountId: 3,
    },
    userAssignCapable: true,
    allowedRequestTaskActions: ['PAYMENT_MARK_AS_PAID'],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(PaymentGuard);
    store = TestBed.inject(PaymentStore);
    sharedStore = TestBed.inject(SharedStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow payment and update the stores', async () => {
    tasksService.getTaskItemInfoByIdUsingGET.mockReturnValueOnce(of(mockRequestTaskItem));
    const storeResetSpy = jest.spyOn(store, 'reset');
    const sharedStoreResetSpy = jest.spyOn(sharedStore, 'reset');

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }))),
    ).resolves.toBeTruthy();

    expect(storeResetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual({
      ...initialState,
      requestTaskId: 1,
      requestId: '2',
      isEditable: true,
      requestTaskItem: mockRequestTaskItem,
    });

    expect(sharedStoreResetSpy).toHaveBeenCalledTimes(1);
    expect(sharedStore.getState()).toEqual({ accountId: 3 });
  });
});
