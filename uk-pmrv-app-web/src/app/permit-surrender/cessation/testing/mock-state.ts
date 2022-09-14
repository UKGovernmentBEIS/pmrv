import { PermitSurrenderState } from '../../store/permit-surrender.state';

export const mockTaskPayload: Pick<
  PermitSurrenderState,
  'payloadType' | 'cessation' | 'cessationCompleted' | 'allowancesSurrenderRequired'
> = {
  payloadType: 'PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD',
  allowancesSurrenderRequired: true,
  cessation: {
    determinationOutcome: 'APPROVED',
    allowancesSurrenderDate: '2012-12-13',
    numberOfSurrenderAllowances: 100,
    annualReportableEmissions: 123.54,
    subsistenceFeeRefunded: true,
    noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
    notes: 'notes',
  },
  cessationCompleted: false,
};

export const mockTaskState: PermitSurrenderState = {
  requestTaskId: 237,
  requestTaskType: 'PERMIT_SURRENDER_CESSATION_SUBMIT',
  daysRemaining: undefined,
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  isEditable: true,
  assignable: true,

  payloadType: 'PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD',
  allowancesSurrenderRequired: mockTaskPayload.allowancesSurrenderRequired,
  cessation: mockTaskPayload.cessation,
  cessationCompleted: mockTaskPayload.cessationCompleted,

  allowedRequestTaskActions: ['PERMIT_SURRENDER_SAVE_CESSATION'],
  userAssignCapable: true,

  requestId: '1',
  requestType: 'PERMIT_SURRENDER',
  competentAuthority: 'ENGLAND',
  accountId: 1,
};

export const mockActionCompletedPayload: Pick<
  PermitSurrenderState,
  | 'payloadType'
  | 'cessation'
  | 'cessationDecisionNotification'
  | 'cessationDecisionNotificationUsersInfo'
  | 'cessationOfficialNotice'
  | 'creationDate'
> = {
  payloadType: 'PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD',
  cessation: mockTaskPayload.cessation,
  cessationDecisionNotification: {
    operators: ['op1', 'op2'],
    signatory: 'reg',
  },
  cessationDecisionNotificationUsersInfo: {
    op1: {
      contactTypes: ['PRIMARY'],
      name: 'Operator1 Name',
    },
    op2: {
      contactTypes: ['PRIMARY'],
      name: 'Operator2 Name',
    },
    reg: {
      name: 'Regulator Name',
    },
  },
  cessationOfficialNotice: {
    name: 'off notice.pdf',
    uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
  },
  creationDate: '2022-02-20 14:26:44',
};

export const mockActionCompletedState: PermitSurrenderState = {
  ...mockActionCompletedPayload,
  requestActionId: 1,
  requestActionType: 'PERMIT_SURRENDER_CESSATION_COMPLETED',
  isEditable: false,

  allowedRequestTaskActions: [],
  userAssignCapable: undefined,
};
