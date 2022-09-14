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
import { NoHighestRequiredTierJustification } from './noHighestRequiredTierJustification';

export interface N2OMeasuredEmissions {
  isHighestRequiredTier?: boolean;
  measurementDevicesOrMethods?: Array<string>;
  noHighestRequiredTierJustification?: NoHighestRequiredTierJustification;
  otherSamplingFrequency?: string;
  samplingFrequency: 'ANNUALLY' | 'BI_ANNUALLY' | 'CONTINUOUS' | 'DAILY' | 'MONTHLY' | 'OTHER' | 'WEEKLY';
  tier: 'TIER_1' | 'TIER_2' | 'TIER_3';
}
