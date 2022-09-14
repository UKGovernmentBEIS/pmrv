import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { TasksService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { LOCAL_STORAGE } from '../core/services/local-storage';
import { SharedModule } from '../shared/shared.module';
import { PermitApplicationGuard } from './permit-application.guard';
import { PermitApplicationStore } from './store/permit-application.store';
import { mockPermitApplyPayload } from './testing/mock-permit-apply-action';
import { mockState } from './testing/mock-state';

describe('PermitApplicationGuard', () => {
  let guard: PermitApplicationGuard;
  let store: PermitApplicationStore;

  const authService = {
    userStatus: new BehaviorSubject<UserStatusDTO>({ roleType: 'OPERATOR' }),
  };
  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TasksService, useValue: tasksService },
      ],
    });
    guard = TestBed.inject(PermitApplicationGuard);
    store = TestBed.inject(PermitApplicationStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit prepare payloads', async () => {
    tasksService.getTaskItemInfoByIdUsingGET.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: { permit: {} } as any,
          type: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
        },
        requestInfo: { competentAuthority: 'ENGLAND' },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    tasksService.getTaskItemInfoByIdUsingGET.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: mockPermitApplyPayload,
          type: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
        },
        userAssignCapable: true,
        requestInfo: { competentAuthority: 'ENGLAND' },
      }),
    );
    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: mockState.requestTaskId }))),
    ).resolves.toBeTruthy();
    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestTaskId).toEqual(mockState.requestTaskId);
    expect(store.getState()).toEqual({
      ...mockState,
      assignable: undefined,
      requestId: undefined,
      requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
      accountId: undefined,
      permitType: undefined,
      reviewSectionsCompleted: {},
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
      isRequestTask: true,
    });
  });

  it('should start and stop subscriptions to the local storage', fakeAsync(async () => {
    const localStorage = TestBed.inject(LOCAL_STORAGE);
    tasksService.getTaskItemInfoByIdUsingGET.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: mockPermitApplyPayload,
          type: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
        },
        userAssignCapable: true,
        requestInfo: { competentAuthority: 'ENGLAND' },
      }),
    );

    await lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: mockState.requestTaskId })));

    const item = `permit/${mockState.requestTaskId}`;

    expect(JSON.parse(localStorage.getItem(item))).toEqual({
      ...mockState,
      assignable: undefined,
      requestId: undefined,
      requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
      accountId: undefined,
      permitType: undefined,
      reviewSectionsCompleted: {},
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
      isRequestTask: true,
    });

    localStorage.setItem(item, JSON.stringify({ ...mockPermitApplyPayload, requestTaskId: mockState.requestTaskId }));
    tick(2000);

    expect(guard.canDeactivate()).toBeTruthy();
  }));
});
