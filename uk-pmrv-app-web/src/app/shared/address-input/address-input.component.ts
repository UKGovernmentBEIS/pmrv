import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AddressDTO } from 'pmrv-api';

import { existingControlContainer } from '../providers/control-container.factory';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-address-input',
  templateUrl: './address-input.component.html',
  viewProviders: [existingControlContainer],
})
export class AddressInputComponent {
  static controlsFactory(address: AddressDTO): Record<keyof AddressDTO, FormControl> {
    return {
      line1: new FormControl(address?.line1, [
        GovukValidators.required('Enter an address'),
        GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
      ]),
      line2: new FormControl(
        address?.line2,
        GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
      ),
      city: new FormControl(address?.city, [
        GovukValidators.required('Enter a town or city'),
        GovukValidators.maxLength(255, 'The city should not be more than 255 characters'),
      ]),
      postcode: new FormControl(address?.postcode, [
        GovukValidators.required('Enter a postcode'),
        GovukValidators.maxLength(64, 'The postcode should not be more than 64 characters'),
      ]),
      country: new FormControl(address?.country, GovukValidators.required('Enter a country')),
    };
  }
}
