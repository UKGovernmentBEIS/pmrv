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

export interface CalculationConversionFactorStandardReferenceSource {
  defaultValue?: string;
  otherTypeDetails?: string;
  type:
    | 'MONITORING_REPORTING_REGULATION_ANNEX_II_SECTION_4_2'
    | 'MONITORING_REPORTING_REGULATION_ANNEX_II_SECTION_4_4'
    | 'MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_9_D'
    | 'OTHER';
}
