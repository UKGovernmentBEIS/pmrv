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

export interface PermitVariationGrantDetermination {
  activationDate?: string;
  annualEmissionsTargets?: { [key: string]: number };
  logChanges: string;
  reason?: string;
  type?: 'DEEMED_WITHDRAWN' | 'GRANTED' | 'REJECTED';
}
