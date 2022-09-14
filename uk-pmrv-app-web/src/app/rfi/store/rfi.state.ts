import {
  FileInfoDTO,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RfiQuestionPayload,
  RfiResponsePayload,
  RfiSubmitPayload,
} from 'pmrv-api';

export interface RfiState {
  requestTaskId: number;
  assignee: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  actionId: number;
  accountId: number;
  requestId?: string;
  rfiAttachments?: { [key: string]: string };
  officialDocument?: FileInfoDTO;
  isEditable: boolean;
  assignable?: boolean;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  rfiSubmitPayload: RfiSubmitPayload;
  rfiQuestionPayload: RfiQuestionPayload;
  rfiResponsePayload: RfiResponsePayload;
  daysRemaining: number;
  requestType?: RequestInfoDTO['type'];
  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
}

export const initialState: RfiState = {
  requestTaskId: undefined,
  assignee: undefined,
  actionId: undefined,
  accountId: undefined,
  requestId: undefined,
  rfiAttachments: {},
  isEditable: true,
  usersInfo: undefined,
  rfiSubmitPayload: undefined,
  rfiQuestionPayload: undefined,
  rfiResponsePayload: undefined,
  daysRemaining: undefined,
};
