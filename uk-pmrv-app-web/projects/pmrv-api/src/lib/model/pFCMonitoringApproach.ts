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
import { CellAndAnodeType } from './cellAndAnodeType';
import { PFCSourceStreamCategoryAppliedTier } from './pFCSourceStreamCategoryAppliedTier';
import { PFCTier2EmissionFactor } from './pFCTier2EmissionFactor';
import { ProcedureForm } from './procedureForm';

export interface PFCMonitoringApproach {
  approachDescription: string;
  cellAndAnodeTypes?: Array<CellAndAnodeType>;
  collectionEfficiency: ProcedureForm;
  sourceStreamCategoryAppliedTiers?: Array<PFCSourceStreamCategoryAppliedTier>;
  tier2EmissionFactor: PFCTier2EmissionFactor;
  type?: 'CALCULATION' | 'FALLBACK' | 'INHERENT_CO2' | 'MEASUREMENT' | 'N2O' | 'PFC' | 'TRANSFERRED_CO2';
}