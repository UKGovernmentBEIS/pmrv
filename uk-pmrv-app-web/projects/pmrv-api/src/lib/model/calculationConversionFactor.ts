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
import { CalculationAnalysisMethod } from './calculationAnalysisMethod';
import { CalculationConversionFactorStandardReferenceSource } from './calculationConversionFactorStandardReferenceSource';
import { NoHighestRequiredTierJustification } from './noHighestRequiredTierJustification';

export interface CalculationConversionFactor {
  analysisMethodUsed?: boolean;
  analysisMethods?: Array<CalculationAnalysisMethod>;
  defaultValueApplied?: boolean;
  exist?: boolean;
  isHighestRequiredTier?: boolean;
  noHighestRequiredTierJustification?: NoHighestRequiredTierJustification;
  standardReferenceSource?: CalculationConversionFactorStandardReferenceSource;
  tier?: 'NO_TIER' | 'TIER_1' | 'TIER_2';
}
