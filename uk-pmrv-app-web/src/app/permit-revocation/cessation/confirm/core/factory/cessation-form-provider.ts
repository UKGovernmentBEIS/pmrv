import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { GovukValidators, MessageValidatorFn } from 'govuk-components';

import { PermitCessation } from 'pmrv-api';

export const PERMIT_REVOCATION_CESSATION_TASK_FORM = new InjectionToken<FormGroup>(
  'Permit revocation cessation task form',
);

export const permitRevocationCessationFormProvider = {
  provide: PERMIT_REVOCATION_CESSATION_TASK_FORM,
  deps: [FormBuilder, PermitRevocationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitRevocationStore, route: ActivatedRoute) => {
    const keys: string[] = route.snapshot.data.keys;
    const skipValidators = route.snapshot.data.skipValidators;
    const formGroupObj = {};

    const state = store.getValue();
    const permitCessation = state.cessation;
    const disabled = !state.isEditable;

    for (const key of keys) {
      if (key) {
        formGroupObj[key] = [
          {
            value:
              permitCessation !== undefined && permitCessation[key] !== undefined && permitCessation[key] !== null
                ? value(permitCessation, key)
                : null,
            disabled,
          },
          { validators: skipValidators ? [] : setValidators(key) },
        ];
      }
    }
    return fb.group(formGroupObj);
  },
};

const value = (permitCessation: PermitCessation, key: string) => {
  return key.toLowerCase().includes('date') ? new Date(permitCessation[key]) : permitCessation[key];
};

const getTwoDecimalPlacesValidator = GovukValidators.pattern(
  '[0-9]*\\.?[0-9]{0,2}',
  'Enter the number of emissions to 2 decimal place',
);

const setValidators = (key: string): MessageValidatorFn[] => {
  switch (key) {
    case 'determinationOutcome':
      return [GovukValidators.required('Select Approved or Rejected')];
    case 'allowancesSurrenderDate':
      return [GovukValidators.required('Enter the date when the installation allowances were surrendered')];
    case 'numberOfSurrenderAllowances':
      return [
        GovukValidators.required('Enter the number of allowances that were surrendered'),
        GovukValidators.min(0, 'The min value is 0'),
        GovukValidators.max(99999999, 'The max limit value is 99,999,999'),
        GovukValidators.pattern('[0-9]*', 'Insert valid number of allowances'),
      ];
    case 'annualReportableEmissions':
      return [
        GovukValidators.required('Enter the annual reportable emissions'),
        GovukValidators.min(0.1, 'Annual reportable emissions tonnes of CO2e must be between 0 and 99,999,999.99'),
        GovukValidators.max(
          99999999.999,
          'Annual reportable emissions tonnes of CO2e must be between 0 and 99,999,999.99',
        ),
        getTwoDecimalPlacesValidator,
      ];
    case 'subsistenceFeeRefunded':
      return [GovukValidators.required('Select Yes or No')];
    case 'noticeType':
      return [GovukValidators.required('Select the text that should appear in the official notice')];
    case 'notes':
      return [
        GovukValidators.required('Enter notes about the cessation'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ];
    default:
      return null;
  }
};
