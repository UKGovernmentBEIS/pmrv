import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { EnvPermitOrLicence } from 'pmrv-api';

import { atLeastOneRequiredValidator } from '../../shared-user/utils/validators';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const otherPermitsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permit.environmentalPermitsAndLicences;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      envPermitOrLicences: fb.array(value?.envPermitOrLicences?.map(createAnotherPermit) ?? [createAnotherPermit()]),
    });
  },
};

export function createAnotherPermit(value?: EnvPermitOrLicence): FormGroup {
  return new FormGroup(
    {
      type: new FormControl(value?.type ?? null, [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')]),
      num: new FormControl(value?.num ?? null, [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')]),
      issuingAuthority: new FormControl(value?.issuingAuthority ?? null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      permitHolder: new FormControl(value?.permitHolder ?? null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
    },
    {
      validators: [
        atLeastOneRequiredValidator(
          'Enter the type or the number or the issuing authority or the permit holder of the permit or licence',
        ),
      ],
    },
  );
}
