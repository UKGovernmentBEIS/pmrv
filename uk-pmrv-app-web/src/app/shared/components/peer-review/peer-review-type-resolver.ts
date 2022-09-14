import { ItemDTO, RequestTaskActionPayload, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

import { UrlRequestType } from '../../types/url-request-type';

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

export function getTaskType(requestType: UrlRequestType): RequestTaskDTO['type'] {
  switch (requestType) {
    case 'permit-application':
      return 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW';
    case 'permit-surrender':
      return 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW';
    case 'permit-revocation':
      return 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW';
    case 'permit-notification':
      return 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW';

    default:
      return null;
  }
}

export function getWaitActionTypes(requestType: UrlRequestType): ItemDTO['taskType'][] {
  switch (requestType) {
    case 'permit-application':
      return ['PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE', 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE'];
    case 'permit-surrender':
      return ['PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE'];
    case 'permit-notification':
      return ['PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE'];

    default:
      return [];
  }
}

export function getRequestTaskActionType(
  requestType: UrlRequestType,
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestType) {
    case 'permit-application':
      return 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW';
    case 'permit-surrender':
      return 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW';
    case 'permit-revocation':
      return 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW';
    case 'permit-notification':
      return 'PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW';

    default:
      return null;
  }
}

export function getRequestTaskActionPayloadType(requestType: UrlRequestType): RequestTaskActionPayload['payloadType'] {
  switch (requestType) {
    case 'permit-application':
      return 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'permit-surrender':
      return 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'permit-revocation':
      return 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'permit-notification':
      return 'PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW_PAYLOAD';

    default:
      return null;
  }
}

export function getPreviousPage(requestType: UrlRequestType): string {
  switch (requestType) {
    case 'permit-application':
      return 'Permit Determination';
    case 'permit-surrender':
      return 'Permit Surrender';
    case 'permit-revocation':
      return 'Permit Revocation';
    case 'permit-notification':
      return 'Permit Notification';

    default:
      return null;
  }
}
