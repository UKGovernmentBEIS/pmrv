import { FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AbbreviationDefinition } from 'pmrv-api';

export function createAbbreviationDefinition(value?: AbbreviationDefinition): FormGroup {
  return new FormGroup({
    abbreviation: new FormControl(value?.abbreviation ?? null, [
      GovukValidators.required('Enter an abbreviation, acronym or term'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    definition: new FormControl(value?.definition ?? null, [
      GovukValidators.required('Enter a definition'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
