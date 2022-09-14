import { RequestCreateActionProcessDTO, RequestDetailsDTO } from 'pmrv-api';

export interface WorkflowLabel {
  title: string;
  expandableTitle: string;
  expandableTitleSummary: string;
  button: string;
  type: RequestCreateActionProcessDTO['requestCreateActionType'];
  errors: string[];
}

export type WorkflowMap = Omit<
  Record<RequestDetailsDTO['requestType'], Partial<WorkflowLabel>>,
  'SYSTEM_MESSAGE_NOTIFICATION'
>;
