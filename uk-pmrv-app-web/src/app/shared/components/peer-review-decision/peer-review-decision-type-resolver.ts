import { UrlRequestType } from '@shared/types/url-request-type';

import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

export function getRequestType(url: string): UrlRequestType {
  const workflowTypes: UrlRequestType[] = [
    'permit-application',
    'permit-surrender',
    'permit-revocation',
    'permit-notification',
  ];
  const requestType = workflowTypes.find((workflowType) => url.includes(workflowType));
  return (requestType as UrlRequestType) ?? null;
}

export function getRequestTaskActionType(
  requestType: UrlRequestType,
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestType) {
    case 'permit-application':
      return 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'permit-surrender':
      return 'PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'permit-revocation':
      return 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION';
    case 'permit-notification':
      return 'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION';

    default:
      return null;
  }
}

export function getRequestTaskActionPayloadType(requestType: UrlRequestType): RequestTaskActionPayload['payloadType'] {
  switch (requestType) {
    case 'permit-application':
      return 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'permit-surrender':
      return 'PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'permit-revocation':
      return 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'permit-notification':
      return 'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';

    default:
      return null;
  }
}

export function getPreviousPage(requestType: UrlRequestType) {
  switch (requestType) {
    case 'permit-application':
      return 'Permit peer review';
    case 'permit-surrender':
      return 'Surrender permit determination';
    case 'permit-revocation':
      return 'Permit revocation';
    case 'permit-notification':
      return 'Permit notification';

    default:
      return null;
  }
}
