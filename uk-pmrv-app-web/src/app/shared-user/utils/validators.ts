import { FormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export function requiredFieldsValidator(): ValidatorFn {
  return GovukValidators.builder('You must fill all required values', (group: FormGroup) =>
    Object.keys(group.controls).find((key) => group.controls[key].hasError('required'))
      ? { emptyRequiredFields: true }
      : null,
  );
}

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: FormGroup) =>
    Object.keys(group.controls).find((key) => !!group.controls[key].value) ? null : { atLeastOneRequired: true },
  );
}
