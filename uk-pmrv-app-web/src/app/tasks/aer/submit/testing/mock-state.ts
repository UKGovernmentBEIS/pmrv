import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

export function mockStateBuild(value?: any, status?: any): CommonTasksState {
  return {
    ...mockState,
    requestTaskItem: {
      ...mockState.requestTaskItem,
      requestTask: {
        ...mockState.requestTaskItem.requestTask,
        payload: {
          ...mockAerApplyPayload,
          aerSectionsCompleted: {
            ...mockAerApplyPayload.aerSectionsCompleted,
            ...status,
          },
          aer: {
            ...mockAerApplyPayload.aer,
            ...value,
          },
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'AER_SAVE_APPLICATION',
    requestTaskId: mockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
      aer: {
        ...mockAerApplyPayload.aer,
        ...value,
      },
      aerSectionsCompleted: {
        ...mockAerApplyPayload.aerSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
