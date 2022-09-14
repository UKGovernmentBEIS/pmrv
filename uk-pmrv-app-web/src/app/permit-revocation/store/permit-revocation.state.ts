import {
  DecisionNotification,
  FileInfoDTO,
  PermitCessation,
  PermitRevocation,
  RequestActionDTO,
  RequestActionPayload,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

export interface PermitRevocationState {
  // request task DTO
  requestTaskId?: number;
  requestTaskType?: RequestTaskDTO['type'];
  requestActionId?: number;
  requestActionType?: RequestActionDTO['type'];

  daysRemaining?: number;

  assignee?: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  isEditable: boolean;
  assignable?: boolean;
  isRequestTask?: boolean;

  //payload
  payloadTaskType: RequestTaskPayload['payloadType'];
  payloadActionType: RequestActionPayload['payloadType'];

  sectionsCompleted?: {
    [key: string]: boolean;
  };

  permitRevocation?: PermitRevocation;
  feeAmount?: number;
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  allowancesSurrenderRequired?: boolean;
  officialNotice?: FileInfoDTO;

  // Represents reason of withdrawing the revocation
  reason?: string;
  revocationAttachments?: { [key: string]: string };
  withdrawFiles?: string[];
  withdrawnOfficialNotice?: FileInfoDTO;

  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  userAssignCapable: boolean;

  //request info DTO
  requestId?: string;
  requestType?: RequestInfoDTO['type'];
  competentAuthority?: RequestInfoDTO['competentAuthority'];
  accountId?: RequestInfoDTO['accountId'];

  //request action info
  creationDate?: string;

  cessationCompleted?: boolean;
  cessation?: PermitCessation;
  cessationDecisionNotification?: DecisionNotification;
  cessationDecisionNotificationUsersInfo?: { [key: string]: RequestActionUserInfo };
  cessationOfficialNotice?: FileInfoDTO;
}

export const initialState: PermitRevocationState = {
  isEditable: undefined,

  payloadTaskType: undefined,
  payloadActionType: undefined,
  allowedRequestTaskActions: [],
  userAssignCapable: undefined,
};
