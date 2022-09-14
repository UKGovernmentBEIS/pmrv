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

export interface RequestActionUserInfo {
  contactTypes?: Array<'CA_SITE' | 'FINANCIAL' | 'PRIMARY' | 'SECONDARY' | 'SERVICE' | 'VB_SITE'>;
  name: string;
  roleCode?: string;
}
