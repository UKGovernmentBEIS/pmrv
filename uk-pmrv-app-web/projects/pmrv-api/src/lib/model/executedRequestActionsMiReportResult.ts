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
import { ExecutedRequestAction } from './executedRequestAction';

export interface ExecutedRequestActionsMiReportResult {
  actions?: Array<ExecutedRequestAction>;
  reportType:
    | 'COMPLETED_WORK'
    | 'LIST_OF_ACCOUNTS'
    | 'LIST_OF_ACCOUNTS_REGULATORS'
    | 'LIST_OF_ACCOUNTS_USERS_CONTACTS'
    | 'LIST_OF_VERIFICATION_BODIES_AND_USERS';
}
