import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

import { initialState, PermitApplicationState } from '../store/permit-application.state';
import { mockPermitApplyPayload } from './mock-permit-apply-action';

export const mockState: PermitApplicationState = {
  ...initialState,
  ...mockPermitApplyPayload,
  reviewSectionsCompleted: {},
  permitType: 'GHGE',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: 237,
  isRequestTask: true,
  isEditable: true,
  isVariation: false,
  assignable: true,
  userViewRole: 'OPERATOR',
};

export const mockRequestActionState: PermitApplicationState = {
  ...initialState,
  ...mockPermitApplyPayload,
  reviewSectionsCompleted: {},
  permitType: 'GHGE',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: undefined,
  actionId: 1,
  isRequestTask: false,
  isEditable: false,
  isVariation: false,
  assignable: false,
  userViewRole: 'OPERATOR',
};

export const mockReviewState: PermitApplicationState = {
  ...initialState,
  ...mockPermitApplyPayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  payloadType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: 237,
  isRequestTask: true,
  isEditable: true,
  isVariation: false,
  assignable: true,
};

export const mockReviewRequestActionState: PermitApplicationState = {
  ...initialState,
  ...mockPermitApplyPayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  payloadType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: undefined,
  actionId: 1,
  isRequestTask: false,
  isEditable: false,
  isVariation: false,
  assignable: false,
};

export const mockVariationSubmitState: PermitApplicationState = {
  ...mockState,
  payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
  requestTaskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
  isVariation: true,
  permitVariationDetails: undefined,
  permitVariationDetailsCompleted: undefined,
};

export function mockStateBuild(value?: any, status?: any): PermitApplicationState {
  return {
    ...mockState,
    permitSectionsCompleted: {
      ...mockState.permitSectionsCompleted,
      ...status,
    },
    permit: {
      ...mockState.permit,
      ...value,
    },
  };
}

export function mockReviewStateBuild(value?: any, status?: any): PermitApplicationState {
  return {
    ...mockReviewState,
    determination: {
      ...mockReviewState.determination,
      ...value,
    },
    reviewSectionsCompleted: {
      ...mockReviewState.reviewSectionsCompleted,
      ...status,
    },
  };
}

export function mockDeterminationPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION',
    requestTaskId: mockReviewState.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination: {
        ...mockState.determination,
        ...value,
      },
      reviewSectionsCompleted: {
        ...mockState.reviewSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
    requestTaskId: mockState.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      permitType: mockState.permitType,
      permit: {
        ...mockState.permit,
        ...value,
      },
      permitSectionsCompleted: {
        ...mockState.permitSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockDecisionPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
    requestTaskId: mockReviewState.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      ...mockReviewState,
      reviewGroupDecisions: {
        ...mockReviewState.reviewGroupDecisions,
        ...value,
      },
      reviewSectionsCompleted: {
        ...mockReviewState.reviewSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
