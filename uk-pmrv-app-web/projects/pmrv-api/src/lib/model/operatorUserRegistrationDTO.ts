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
import { PhoneNumberDTO } from './phoneNumberDTO';

export interface OperatorUserRegistrationDTO {
  address?: AddressDTO;
  emailToken: string;
  firstName: string;
  lastName: string;
  mobileNumber?: PhoneNumberDTO;
  phoneNumber?: PhoneNumberDTO;
  termsVersion?: number;
}