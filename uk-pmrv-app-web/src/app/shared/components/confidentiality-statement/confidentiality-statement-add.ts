import { FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ConfidentialSection } from 'pmrv-api';

export function createAnotherSection(value?: ConfidentialSection): FormGroup {
  return new FormGroup({
    section: new FormControl(value?.section ?? null, [
      GovukValidators.required('Enter a section'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    explanation: new FormControl(value?.explanation ?? null, [
      GovukValidators.required('Enter an explanation'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
