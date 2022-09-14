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
import { AddressDTO } from './addressDTO';

export interface LegalEntityDTO {
  address?: AddressDTO;
  id?: number;
  name?: string;
  noReferenceNumberReason?: string;
  referenceNumber?: string;
  type?: 'LIMITED_COMPANY' | 'OTHER' | 'PARTNERSHIP' | 'SOLE_TRADER';
}