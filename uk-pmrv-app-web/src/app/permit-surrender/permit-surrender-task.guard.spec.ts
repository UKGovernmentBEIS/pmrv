import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { PermitSurrenderApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, MockType } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { PermitSurrenderTaskGuard } from './permit-surrender-task.guard';
import { PermitSurrenderStore } from './store/permit-surrender.store';
import { mockTaskCompletedPayload, mockTaskState } from './testing/mock-state';

describe('PermitSurrenderTaskGuard', () => {
  let guard: PermitSurrenderTaskGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {
    getTaskItemInfoByIdUsingGET: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(PermitSurrenderTaskGuard);
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit surrender payloads', async () => {
    tasksService.getTaskItemInfoByIdUsingGET.mockReturnValueOnce(
      of({
        requestTask: {
          id: mockTaskState.requestTaskId,
          type: 'PERMIT_SURRENDER_APPLICATION_SUBMIT',
          payload: {},
          assigneeFullName: mockTaskState.assignee.assigneeFullName,
          assigneeUserId: mockTaskState.assignee.assigneeUserId,
        },
        allowedRequestTaskActions: mockTaskState.allowedRequestTaskActions,
        requestInfo: { competentAuthority: mockTaskState.competentAuthority },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: `${mockTaskState.requestTaskId}` }))),
    ).resolves.toBeTruthy();

    expect(tasksService.getTaskItemInfoByIdUsingGET).toHaveBeenCalledTimes(1);
    expect(tasksService.getTaskItemInfoByIdUsingGET).toHaveBeenLastCalledWith(mockTaskState.requestTaskId);
  });

  it('should update store', async () => {
    tasksService.getTaskItemInfoByIdUsingGET.mockReturnValueOnce(
      of({
        requestTask: {
          id: mockTaskState.requestTaskId,
          type: 'PERMIT_SURRENDER_APPLICATION_SUBMIT',
          payload: mockTaskCompletedPayload as PermitSurrenderApplicationSubmitRequestTaskPayload,
          assignable: mockTaskState.assignable,
          assigneeFullName: mockTaskState.assignee.assigneeFullName,
          assigneeUserId: mockTaskState.assignee.assigneeUserId,
          daysRemaining: mockTaskState.daysRemaining,
        },
        allowedRequestTaskActions: mockTaskState.allowedRequestTaskActions,
        userAssignCapable: true,
        requestInfo: {
          id: '1',
          type: 'PERMIT_SURRENDER',
          competentAuthority: mockTaskState.competentAuthority,
          accountId: mockTaskState.accountId,
        },
      }),
    );

    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: `${mockTaskState.requestTaskId}` }))),
    ).resolves.toBeTruthy();

    expect(tasksService.getTaskItemInfoByIdUsingGET).toHaveBeenCalledTimes(1);
    expect(tasksService.getTaskItemInfoByIdUsingGET).toHaveBeenLastCalledWith(mockTaskState.requestTaskId);

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual(mockTaskState);
  });
});
