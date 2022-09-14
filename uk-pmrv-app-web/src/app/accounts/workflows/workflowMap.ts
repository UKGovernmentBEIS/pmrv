import { RequestDetailsDTO, RequestSearchCriteria } from 'pmrv-api';

export const workflowTypesMap: Omit<
  Record<RequestDetailsDTO['requestType'], string>,
  'SYSTEM_MESSAGE_NOTIFICATION' | 'PERMIT_NOTIFICATION_FOLLOW_UP'
> = {
  PERMIT_ISSUANCE: 'Permit application',
  PERMIT_NOTIFICATION: 'Notification',
  PERMIT_VARIATION: 'Variation',
  PERMIT_REVOCATION: 'Revocation',
  PERMIT_SURRENDER: 'Surrender',
  PERMIT_TRANSFER: 'Transfer',
  INSTALLATION_ACCOUNT_OPENING: 'Account creation',
  AER: 'AER',
};

export const workflowDetailsTypesMap: Omit<
  Record<RequestDetailsDTO['requestType'], string>,
  'SYSTEM_MESSAGE_NOTIFICATION' | 'PERMIT_NOTIFICATION_FOLLOW_UP'
> = {
  INSTALLATION_ACCOUNT_OPENING: 'Account creation',
  PERMIT_ISSUANCE: 'Permit application',
  PERMIT_SURRENDER: 'Permit surrender',
  PERMIT_TRANSFER: 'Permit transfer',
  PERMIT_VARIATION: 'Permit variation',
  PERMIT_REVOCATION: 'Permit revocation',
  PERMIT_NOTIFICATION: 'Permit notification',
  AER: 'AER',
};

export const workflowStatusesMap: Record<RequestSearchCriteria['status'], string> = {
  OPEN: 'Open workflows',
  CLOSED: 'Closed workflows',
};

export const workflowStatusesTagMap: Record<RequestDetailsDTO['requestStatus'], string> = {
  IN_PROGRESS: 'IN PROGRESS',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  WITHDRAWN: 'WITHDRAWN',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
};
