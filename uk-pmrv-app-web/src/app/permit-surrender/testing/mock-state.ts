import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PermitSurrenderState } from '../store/permit-surrender.state';

export const mockTaskCompletedPayload: Pick<
  PermitSurrenderState,
  | 'payloadType'
  | 'permitSurrender'
  | 'permitSurrenderAttachments'
  | 'sectionsCompleted'
  | 'reviewDetermination'
  | 'reviewDeterminationCompleted'
  | 'reviewDecision'
> = {
  payloadType: 'PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD',
  permitSurrender: {
    stopDate: '2012-12-13',
    justification: 'justify',
    documentsExist: true,
    documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd'],
  },
  sectionsCompleted: {
    SURRENDER_APPLY: true,
  },
  permitSurrenderAttachments: {
    '2c30c8bf-3d5e-474d-98a0-123a87eb60dd': 'test.jpg',
  },

  reviewDecision: {
    type: 'ACCEPTED',
    notes: 'accepted notes',
  },

  reviewDeterminationCompleted: true,
  reviewDetermination: {
    type: 'GRANTED',
    reason: 'reason',
    stopDate: '2012-12-13',
    noticeDate: '2030-12-13',
    reportRequired: false,
    allowancesSurrenderRequired: false,
  } as PermitSurrenderReviewDeterminationGrant,
};

export const mockTaskState: PermitSurrenderState = {
  requestTaskId: 237,
  requestTaskType: 'PERMIT_SURRENDER_APPLICATION_SUBMIT',
  daysRemaining: 58,
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  isEditable: true,
  assignable: true,

  payloadType: 'PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD',
  permitSurrender: mockTaskCompletedPayload.permitSurrender,
  permitSurrenderAttachments: mockTaskCompletedPayload.permitSurrenderAttachments,
  sectionsCompleted: mockTaskCompletedPayload.sectionsCompleted,

  reviewDecision: mockTaskCompletedPayload.reviewDecision,
  reviewDeterminationCompleted: mockTaskCompletedPayload.reviewDeterminationCompleted,
  reviewDetermination: mockTaskCompletedPayload.reviewDetermination,

  allowedRequestTaskActions: ['PERMIT_SURRENDER_SAVE_APPLICATION'],
  userAssignCapable: true,

  requestId: '1',
  requestType: 'PERMIT_SURRENDER',
  competentAuthority: 'ENGLAND',
  accountId: 1,
};

export const mockActionSubmittedPayload: Pick<
  PermitSurrenderState,
  'payloadType' | 'permitSurrender' | 'permitSurrenderAttachments'
> = {
  payloadType: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD',
  permitSurrender: {
    stopDate: '2012-12-13',
    justification: 'justify',
    documentsExist: true,

    documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd'],
  },
  permitSurrenderAttachments: {
    '2c30c8bf-3d5e-474d-98a0-123a87eb60dd': 'test.jpg',
  },
};

export const mockActionState: PermitSurrenderState = {
  requestActionId: 1,
  requestActionType: 'PERMIT_SURRENDER_APPLICATION_SUBMITTED',
  isEditable: false,

  payloadType: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD',
  permitSurrender: mockActionSubmittedPayload.permitSurrender,
  permitSurrenderAttachments: mockActionSubmittedPayload.permitSurrenderAttachments,

  allowedRequestTaskActions: [],
  userAssignCapable: undefined,

  creationDate: '2022-01-22T13:34:52.944920Z',
};
