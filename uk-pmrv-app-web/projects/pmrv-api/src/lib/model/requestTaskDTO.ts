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
import { RequestTaskPayload } from './requestTaskPayload';

export interface RequestTaskDTO {
  assignable?: boolean;
  assigneeFullName?: string;
  assigneeUserId?: string;
  daysRemaining?: number;
  endDate?: string;
  id?: number;
  payload?: RequestTaskPayload;
  startDate?: string;
  type?:
    | 'ACCOUNT_USERS_SETUP'
    | 'AER_APPLICATION_AMENDS_SUBMIT'
    | 'AER_APPLICATION_REVIEW'
    | 'AER_APPLICATION_SUBMIT'
    | 'AER_APPLICATION_VERIFICATION_SUBMIT'
    | 'AER_WAIT_FOR_AMENDS'
    | 'AER_WAIT_FOR_REVIEW'
    | 'AER_WAIT_FOR_VERIFICATION'
    | 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW'
    | 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE'
    | 'NEW_VERIFICATION_BODY_INSTALLATION'
    | 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
    | 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW'
    | 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
    | 'PERMIT_ISSUANCE_APPLICATION_SUBMIT'
    | 'PERMIT_ISSUANCE_CONFIRM_PAYMENT'
    | 'PERMIT_ISSUANCE_MAKE_PAYMENT'
    | 'PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT'
    | 'PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT'
    | 'PERMIT_ISSUANCE_TRACK_PAYMENT'
    | 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS'
    | 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW'
    | 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE'
    | 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW'
    | 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE'
    | 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW'
    | 'PERMIT_NOTIFICATION_APPLICATION_REVIEW'
    | 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW'
    | 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT'
    | 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP'
    | 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW'
    | 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW'
    | 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE'
    | 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW'
    | 'PERMIT_REVOCATION_APPLICATION_SUBMIT'
    | 'PERMIT_REVOCATION_CESSATION_SUBMIT'
    | 'PERMIT_REVOCATION_CONFIRM_PAYMENT'
    | 'PERMIT_REVOCATION_MAKE_PAYMENT'
    | 'PERMIT_REVOCATION_TRACK_PAYMENT'
    | 'PERMIT_REVOCATION_WAIT_FOR_APPEAL'
    | 'PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW'
    | 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW'
    | 'PERMIT_SURRENDER_APPLICATION_REVIEW'
    | 'PERMIT_SURRENDER_APPLICATION_SUBMIT'
    | 'PERMIT_SURRENDER_CESSATION_SUBMIT'
    | 'PERMIT_SURRENDER_CONFIRM_PAYMENT'
    | 'PERMIT_SURRENDER_MAKE_PAYMENT'
    | 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT'
    | 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT'
    | 'PERMIT_SURRENDER_TRACK_PAYMENT'
    | 'PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW'
    | 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE'
    | 'PERMIT_SURRENDER_WAIT_FOR_REVIEW'
    | 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE'
    | 'PERMIT_VARIATION_APPLICATION_REVIEW'
    | 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT'
    | 'PERMIT_VARIATION_RDE_RESPONSE_SUBMIT'
    | 'PERMIT_VARIATION_REGULATOR_APPLICATION_SUBMIT'
    | 'PERMIT_VARIATION_RFI_RESPONSE_SUBMIT'
    | 'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE'
    | 'PERMIT_VARIATION_WAIT_FOR_REVIEW'
    | 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE'
    | 'VERIFIER_NO_LONGER_AVAILABLE';
}
