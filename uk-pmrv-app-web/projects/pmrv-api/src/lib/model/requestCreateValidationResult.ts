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

export interface RequestCreateValidationResult {
  accountStatus?:
    | 'AWAITING_RATIONALISATION'
    | 'AWAITING_REVOCATION'
    | 'AWAITING_SURRENDER'
    | 'AWAITING_TRANSFER'
    | 'CEASED_OPERATIONS'
    | 'COMMISSION_LIST'
    | 'DEEMED_WITHDRAWN'
    | 'DENIED'
    | 'EXEMPT'
    | 'EXEMPT_COMMERCIAL'
    | 'EXEMPT_NON_COMMERCIAL'
    | 'LIVE'
    | 'NEW'
    | 'PERMIT_REFUSED'
    | 'PRIOR_COMPLIANCE_LIST'
    | 'RATIONALISED'
    | 'REMOVED_FROM_COMMISSION_LIST'
    | 'REVOKED'
    | 'SURRENDERED'
    | 'TRANSFERRED'
    | 'UNAPPROVED';
  applicableAccountStatuses?: Array<
    | 'AWAITING_RATIONALISATION'
    | 'AWAITING_REVOCATION'
    | 'AWAITING_SURRENDER'
    | 'AWAITING_TRANSFER'
    | 'CEASED_OPERATIONS'
    | 'COMMISSION_LIST'
    | 'DEEMED_WITHDRAWN'
    | 'DENIED'
    | 'EXEMPT'
    | 'EXEMPT_COMMERCIAL'
    | 'EXEMPT_NON_COMMERCIAL'
    | 'LIVE'
    | 'NEW'
    | 'PERMIT_REFUSED'
    | 'PRIOR_COMPLIANCE_LIST'
    | 'RATIONALISED'
    | 'REMOVED_FROM_COMMISSION_LIST'
    | 'REVOKED'
    | 'SURRENDERED'
    | 'TRANSFERRED'
    | 'UNAPPROVED'
  >;
  requests?: Array<
    | 'AER'
    | 'INSTALLATION_ACCOUNT_OPENING'
    | 'PERMIT_ISSUANCE'
    | 'PERMIT_NOTIFICATION'
    | 'PERMIT_NOTIFICATION_FOLLOW_UP'
    | 'PERMIT_REVOCATION'
    | 'PERMIT_SURRENDER'
    | 'PERMIT_TRANSFER'
    | 'PERMIT_VARIATION'
    | 'SYSTEM_MESSAGE_NOTIFICATION'
  >;
  valid?: boolean;
}
