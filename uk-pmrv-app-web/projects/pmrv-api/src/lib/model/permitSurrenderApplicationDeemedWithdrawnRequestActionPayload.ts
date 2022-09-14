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
import { DecisionNotification } from './decisionNotification';
import { FileInfoDTO } from './fileInfoDTO';
import { PermitSurrenderReviewDeterminationDeemWithdraw } from './permitSurrenderReviewDeterminationDeemWithdraw';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload {
  officialNotice: FileInfoDTO;
  payloadType?:
    | 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD'
    | 'INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD'
    | 'INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD'
    | 'PAYMENT_CANCELLED_PAYLOAD'
    | 'PAYMENT_COMPLETED_PAYLOAD'
    | 'PAYMENT_MARKED_AS_PAID_PAYLOAD'
    | 'PAYMENT_MARKED_AS_RECEIVED_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_GRANTED_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_REJECTED_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD'
    | 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD'
    | 'PERMIT_ISSUANCE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD'
    | 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD'
    | 'PERMIT_NOTIFICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD'
    | 'PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD'
    | 'PERMIT_REVOCATION_APPLICATION_WITHDRAWN_PAYLOAD'
    | 'PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD'
    | 'PERMIT_REVOCATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD'
    | 'PERMIT_SURRENDER_APPLICATION_SUBMITTED_PAYLOAD'
    | 'PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD'
    | 'PERMIT_SURRENDER_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD'
    | 'PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD'
    | 'RDE_DECISION_FORCED_PAYLOAD'
    | 'RDE_REJECTED_PAYLOAD'
    | 'RDE_SUBMITTED_PAYLOAD'
    | 'RFI_RESPONSE_SUBMITTED_PAYLOAD'
    | 'RFI_SUBMITTED_PAYLOAD';
  reviewDecisionNotification: DecisionNotification;
  reviewDetermination: PermitSurrenderReviewDeterminationDeemWithdraw;
  usersInfo?: { [key: string]: RequestActionUserInfo };
}
