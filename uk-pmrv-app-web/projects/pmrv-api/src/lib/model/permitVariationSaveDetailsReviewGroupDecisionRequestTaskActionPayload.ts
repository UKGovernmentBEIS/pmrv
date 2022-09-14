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
import { PermitVariationReviewDecision } from './permitVariationReviewDecision';

export interface PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload {
  decision: PermitVariationReviewDecision;
  payloadType?:
    | 'AER_SAVE_APPLICATION_PAYLOAD'
    | 'EMPTY_PAYLOAD'
    | 'INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION_PAYLOAD'
    | 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD'
    | 'PAYMENT_CANCEL_PAYLOAD'
    | 'PAYMENT_MARK_AS_RECEIVED_PAYLOAD'
    | 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD'
    | 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD'
    | 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD'
    | 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD'
    | 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD'
    | 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD'
    | 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND_PAYLOAD'
    | 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD'
    | 'PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD'
    | 'PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD'
    | 'PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD'
    | 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION_PAYLOAD'
    | 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD'
    | 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL_PAYLOAD'
    | 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD'
    | 'PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD'
    | 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD'
    | 'PERMIT_REVOCATION_WITHDRAW_APPLICATION_PAYLOAD'
    | 'PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD'
    | 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD'
    | 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD'
    | 'PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD'
    | 'PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD'
    | 'PERMIT_SURRENDER_SAVE_CESSATION_PAYLOAD'
    | 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD'
    | 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD'
    | 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION_PAYLOAD'
    | 'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD'
    | 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD'
    | 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD'
    | 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD'
    | 'RDE_FORCE_DECISION_PAYLOAD'
    | 'RDE_RESPONSE_SUBMIT_PAYLOAD'
    | 'RDE_SUBMIT_PAYLOAD'
    | 'RFI_RESPONSE_SUBMIT_PAYLOAD'
    | 'RFI_SUBMIT_PAYLOAD';
  permitVariationDetailsReviewCompleted?: boolean;
  reviewSectionsCompleted?: { [key: string]: boolean };
}
