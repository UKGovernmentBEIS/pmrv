import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import {
  PermitVariationApplicationSubmitRequestTaskPayload,
  RequestActionsService,
  RequestItemsService,
  TasksService,
} from 'pmrv-api';

import { mockClass } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { SharedModule } from '../../shared/shared.module';
import { CommonTasksStore } from '../store/common-tasks.store';
import { PermitVariationGuard } from './permit-variation.guard';

describe('PermitVariationGuard', () => {
  let guard: PermitVariationGuard;
  let permitApplicationStore: PermitApplicationStore;
  let commonTasksStore: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);
  const authService = mockClass(AuthService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: AuthService, useValue: authService },
      ],
    });
    guard = TestBed.inject(PermitVariationGuard);
    commonTasksStore = TestBed.inject(CommonTasksStore);
    permitApplicationStore = TestBed.inject(PermitApplicationStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should update permit application store', async () => {
    commonTasksStore.setState({
      requestTaskItem: {
        allowedRequestTaskActions: ['PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION'],
        requestInfo: { id: '1', type: 'PERMIT_VARIATION', competentAuthority: 'ENGLAND', accountId: 2 },
        requestTask: {
          id: 3,
          assignable: true,
          daysRemaining: 4,
          assigneeUserId: 'userId',
          type: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
            permitType: 'GHGE',
            permitSectionsCompleted: { section1: [true], section2: [true] },
            reviewSectionsCompleted: { review1: false },
          } as PermitVariationApplicationSubmitRequestTaskPayload,
        },
      },
      relatedTasks: [],
      timeLineActions: [],
      storeInitialized: true,
      isEditable: true,
      user: { userId: 'userId' },
    });

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();

    expect(permitApplicationStore.getState()).toEqual({
      requestTaskId: 3,
      requestTaskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
      isRequestTask: true,
      isVariation: true,
      isEditable: true,
      assignable: false,
      userAssignCapable: false,
      assignee: undefined,
      reviewGroupDecisions: undefined,
      competentAuthority: 'ENGLAND',
      accountId: 2,
      daysRemaining: 4,
      payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
      permitType: 'GHGE',
      permitSectionsCompleted: { section1: [true], section2: [true] },
      reviewSectionsCompleted: { review1: false },
    });
  });
});
