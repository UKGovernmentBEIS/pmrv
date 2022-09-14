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
import { RequestCreateActionPayload } from './requestCreateActionPayload';

export interface RequestCreateActionProcessDTO {
  requestCreateActionPayload: RequestCreateActionPayload;
  requestCreateActionType:
    | 'AER'
    | 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION'
    | 'PERMIT_NOTIFICATION'
    | 'PERMIT_REVOCATION'
    | 'PERMIT_SURRENDER'
    | 'PERMIT_TRANSFER'
    | 'PERMIT_VARIATION';
}