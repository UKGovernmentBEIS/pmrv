import { Pipe, PipeTransform } from '@angular/core';

import { PhoneNumberUtil } from 'google-libphonenumber';

import { UKCountryCodes } from '../types/country-codes';

@Pipe({
  name: 'phoneNumber',
})
export class PhoneNumberPipe implements PipeTransform {
  transform(callingCode: string): string {
    const countryCode = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(Number(callingCode));
    return `${UKCountryCodes.GB === countryCode ? UKCountryCodes.UK : countryCode} (${callingCode})`;
  }
}
