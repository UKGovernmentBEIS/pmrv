import {
  FileInfoDTO,
  RdeForceDecisionPayload,
  RdePayload,
  RdeResponsePayload,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export interface RdeState {
  requestTaskId: number;
  assignee: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  actionId: number;
  accountId: number;
  requestId?: string;
  rdeAttachments?: { [key: string]: string };

  rdePayload: RdePayload;
  rdeResponsePayload: RdeResponsePayload;
  reason?: string;
  rdeForceDecisionPayload: RdeForceDecisionPayload;

  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialDocument?: FileInfoDTO;

  daysRemaining: number;
  requestType?: RequestInfoDTO['type'];
  isEditable: boolean;
}

export const initialState: RdeState = {
  requestTaskId: undefined,
  assignee: undefined,
  actionId: undefined,
  accountId: undefined,
  requestId: undefined,
  rdeAttachments: {},

  rdePayload: undefined,
  rdeResponsePayload: undefined,
  rdeForceDecisionPayload: undefined,

  usersInfo: undefined,

  daysRemaining: undefined,
  isEditable: true,
};
