/**
 * PMRV API Documentation
 * Back-end REST API documentation for the UK PMRV application
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.58.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { PermitNotification } from './permitNotification';
import { PermitNotificationReviewDecision } from './permitNotificationReviewDecision';

export interface PermitNotificationApplicationReviewRequestTaskPayload {
  payloadType?:
    | 'AER_APPLICATION_SUBMIT_PAYLOAD'
    | 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD'
    | 'PAYMENT_CONFIRM_PAYLOAD'
    | 'PAYMENT_MAKE_PAYLOAD'
    | 'PAYMENT_TRACK_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD'
    | 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS_PAYLOAD'
    | 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD'
    | 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD'
    | 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD'
    | 'PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD'
    | 'PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD'
    | 'PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD'
    | 'PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD'
    | 'PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD'
    | 'RDE_RESPONSE_SUBMIT_PAYLOAD'
    | 'RDE_WAIT_FOR_RESPONSE_PAYLOAD'
    | 'RFI_RESPONSE_SUBMIT_PAYLOAD'
    | 'SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD';
  permitNotification: PermitNotification;
  permitNotificationAttachments?: { [key: string]: string };
  reviewDecision: PermitNotificationReviewDecision;
  rfiAttachments?: { [key: string]: string };
}
