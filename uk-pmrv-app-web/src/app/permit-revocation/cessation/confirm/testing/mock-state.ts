import { PermitRevocationState } from '../../../store/permit-revocation.state';

export const mockTaskPayload: Pick<
  PermitRevocationState,
  'payloadTaskType' | 'cessation' | 'cessationCompleted' | 'allowancesSurrenderRequired'
> = {
  payloadTaskType: 'PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD',
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

export const mockTaskState: PermitRevocationState = {
  requestTaskId: 237,
  requestTaskType: 'PERMIT_REVOCATION_CESSATION_SUBMIT',
  daysRemaining: undefined,
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  isEditable: true,
  assignable: true,
  payloadActionType: undefined,
  payloadTaskType: 'PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD',
  allowancesSurrenderRequired: mockTaskPayload.allowancesSurrenderRequired,
  cessation: mockTaskPayload.cessation,
  cessationCompleted: mockTaskPayload.cessationCompleted,

  allowedRequestTaskActions: ['PERMIT_REVOCATION_SAVE_CESSATION'],
  userAssignCapable: true,

  requestId: '1',
  requestType: 'PERMIT_REVOCATION',
  competentAuthority: 'ENGLAND',
  accountId: 1,
};

export const mockActionCompletedPayload: Pick<
  PermitRevocationState,
  | 'payloadActionType'
  | 'cessation'
  | 'cessationDecisionNotification'
  | 'cessationDecisionNotificationUsersInfo'
  | 'cessationOfficialNotice'
  | 'creationDate'
> = {
  payloadActionType: 'PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD',
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
    name: 'cessation off notice.pdf',
    uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
  },
  creationDate: '2022-02-20 14:26:44',
};

export const mockActionCompletedState: PermitRevocationState = {
  ...mockActionCompletedPayload,
  requestActionId: 1,
  requestActionType: 'PERMIT_REVOCATION_CESSATION_COMPLETED',
  isEditable: false,

  allowedRequestTaskActions: [],
  userAssignCapable: undefined,
  payloadTaskType: 'PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD',
};
