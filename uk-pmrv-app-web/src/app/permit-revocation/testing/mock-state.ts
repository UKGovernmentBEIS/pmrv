import { PermitRevocationState } from '../store/permit-revocation.state';

export const mockTaskState: PermitRevocationState = {
  isEditable: true,
  allowedRequestTaskActions: ['PERMIT_REVOCATION_SAVE_APPLICATION', 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION'],
  payloadTaskType: undefined,
  payloadActionType: undefined,
  userAssignCapable: true,
  requestTaskId: 13,
  requestTaskType: 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  assignable: true,
  isRequestTask: true,
  requestId: 'AEMR2-1',
  requestType: 'PERMIT_REVOCATION',
  competentAuthority: 'ENGLAND',
  accountId: 2,
  cessationCompleted: undefined,
  cessation: undefined,
};

export const mockActionSubmittedPayload: Pick<PermitRevocationState, 'payloadActionType' | 'permitRevocation'> = {
  payloadActionType: 'PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD',
  permitRevocation: {
    reason: 'test',
    activitiesStopped: true,
    stoppedDate: '2022-04-20',
    effectiveDate: '2022-05-22',
    annualEmissionsReportRequired: true,
    annualEmissionsReportDate: '2022-04-22',
    surrenderRequired: false,
    feeCharged: false,
  },
};

export const mockActionState: PermitRevocationState = {
  requestActionId: 13,
  requestActionType: 'PERMIT_REVOCATION_APPLICATION_SUBMITTED',
  isEditable: false,

  payloadActionType: 'PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD',
  payloadTaskType: undefined,
  permitRevocation: mockActionSubmittedPayload.permitRevocation,

  allowedRequestTaskActions: [],
  userAssignCapable: undefined,

  creationDate: '2022-04-21T14:52:46.363672Z',
};
