import {
  DecisionNotification,
  FileInfoDTO,
  PermitCessation,
  PermitSurrender,
  PermitSurrenderReviewDecision,
  PermitSurrenderReviewDetermination,
  RequestActionDTO,
  RequestActionPayload,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

export interface PermitSurrenderState {
  // request task DTO
  requestTaskId?: number;
  requestTaskType?: RequestTaskDTO['type'];
  requestActionId?: number;
  requestActionType?: RequestActionDTO['type'];

  daysRemaining?: number;

  assignee?: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  isEditable: boolean;
  assignable?: boolean;

  //payload
  payloadType: RequestTaskPayload['payloadType'] | RequestActionPayload['payloadType'];
  permitSurrender?: PermitSurrender;
  permitSurrenderAttachments?: {
    [key: string]: string;
  };
  sectionsCompleted?: {
    [key: string]: boolean;
  };
  reviewDecision?: PermitSurrenderReviewDecision;
  reviewDecisionNotification?: DecisionNotification;
  reviewDetermination?: PermitSurrenderReviewDetermination;
  reviewDeterminationCompleted?: boolean;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice?: FileInfoDTO;

  cessation?: PermitCessation;
  cessationCompleted?: boolean;
  allowancesSurrenderRequired?: boolean;
  cessationDecisionNotification?: DecisionNotification;
  cessationDecisionNotificationUsersInfo?: { [key: string]: RequestActionUserInfo };
  cessationOfficialNotice?: FileInfoDTO;

  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  userAssignCapable: boolean;

  //request info DTO
  requestId?: string;
  requestType?: RequestInfoDTO['type'];
  competentAuthority?: RequestInfoDTO['competentAuthority'];
  accountId?: RequestInfoDTO['accountId'];

  //request action info
  creationDate?: string;
}

export const initialState: PermitSurrenderState = {
  isEditable: undefined,

  payloadType: undefined,

  allowedRequestTaskActions: [],
  userAssignCapable: undefined,
};
