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

export interface TemporaryChange {
  description: string;
  documents?: Array<string>;
  endDateOfNonCompliance?: string;
  justification: string;
  startDateOfNonCompliance?: string;
  type?: 'NON_SIGNIFICANT_CHANGE' | 'OTHER_FACTOR' | 'TEMPORARY_CHANGE' | 'TEMPORARY_FACTOR' | 'TEMPORARY_SUSPENSION';
}
