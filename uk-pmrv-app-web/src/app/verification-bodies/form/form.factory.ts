import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AddressInputComponent } from '../../shared/address-input/address-input.component';

export const VERIFICATION_BODY_FORM = new InjectionToken<FormGroup>('Verification body form');

export const verificationBodyFormFactory: FactoryProvider = {
  provide: VERIFICATION_BODY_FORM,
  useFactory: (fb: FormBuilder) =>
    fb.group({
      details: fb.group({
        name: [
          null,
          [
            GovukValidators.required('Enter the name of the verification body organisation'),
            GovukValidators.maxLength(255, 'Your first name should not be larger than 255 characters'),
          ],
        ],
        accreditationRefNum: [
          null,
          [
            GovukValidators.required('Enter the Accreditation reference number'),
            GovukValidators.maxLength(
              25,
              'Accreditation reference number must not be more than 25 characters long, including spaces',
            ),
          ],
        ],
        address: fb.group(AddressInputComponent.controlsFactory(null)),
      }),
      types: [[], GovukValidators.required('Select at least one type of verification body')],
    }),
  deps: [FormBuilder],
};
