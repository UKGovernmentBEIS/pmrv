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
import { FallbackSourceStreamCategoryAppliedTier } from './fallbackSourceStreamCategoryAppliedTier';
import { ProcedureForm } from './procedureForm';

export interface FallbackMonitoringApproach {
  annualUncertaintyAnalysis: ProcedureForm;
  approachDescription: string;
  justification: string;
  sourceStreamCategoryAppliedTiers?: Array<FallbackSourceStreamCategoryAppliedTier>;
  type?: 'CALCULATION' | 'FALLBACK' | 'INHERENT_CO2' | 'MEASUREMENT' | 'N2O' | 'PFC' | 'TRANSFERRED_CO2';
}
