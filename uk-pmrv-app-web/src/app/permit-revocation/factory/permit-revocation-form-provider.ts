import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import moment from 'moment';

import { GovukValidators, MessageValidatorFn } from 'govuk-components';

import { PermitRevocation } from 'pmrv-api';

export const PERMIT_REVOCATION_TASK_FORM = new InjectionToken<FormGroup>('Permit revocation task form');

let effectiveDate: string;

export const permitRevocationFormProvider = {
  provide: PERMIT_REVOCATION_TASK_FORM,
  deps: [FormBuilder, PermitRevocationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitRevocationStore, route: ActivatedRoute) => {
    const keys: string[] = route.snapshot.data.keys;
    const skipValidators = route.snapshot.data.skipValidators;
    const formGroupObj = {};

    const state = store.getValue();
    const permitRevocation = state.permitRevocation;
    const disabled = !state.isEditable;
    effectiveDate = permitRevocation?.effectiveDate;

    for (const key of keys) {
      if (key) {
        formGroupObj[key] = [
          {
            value:
              permitRevocation !== undefined && permitRevocation[key] !== undefined && permitRevocation[key] !== null
                ? value(permitRevocation, key)
                : null,
            disabled,
          },
          { validators: skipValidators ? [] : addValidators(key) },
        ];
      }
    }
    return fb.group(formGroupObj);
  },
};

export const effectiveDateMinValidator = (): ValidatorFn => {
  return (group: FormGroup): ValidationErrors => {
    const after28Days = moment().add(28, 'd').set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
    return moment(group.value?.effectiveDate ? group.value.effectiveDate : group.value).isAfter(after28Days)
      ? null
      : {
          invalidEffectiveDate: `The effective date of the notice must be at least 28 days after today`,
        };
  };
};

export const feeDateMinValidator = (): ValidatorFn => {
  return (group: FormGroup): ValidationErrors => {
    const govukDatePipe = new GovukDatePipe();
    const currentEffectiveDate = moment(new Date(effectiveDate));
    return moment(group.value?.feeDate ? group.value.feeDate : group.value).isAfter(currentEffectiveDate)
      ? null
      : {
          invalidFeeDate: `The payment due date must be after ${govukDatePipe.transform(
            new Date(currentEffectiveDate.utc(true).toISOString()),
          )}`,
        };
  };
};

const value = (permitRevocation: PermitRevocation, key: string) => {
  return key.toLowerCase().includes('date') ? new Date(permitRevocation[key]) : permitRevocation[key];
};

const addValidators = (key: string): MessageValidatorFn[] => {
  switch (key) {
    case 'activitiesStopped':
      return [GovukValidators.required('Select Yes or No')];
    case 'stoppedDate':
      return [GovukValidators.required('Enter the date when regulated activities stopped')];
    case 'reason':
      return [GovukValidators.required('Explain the reason why the permit is being revoked')];
    case 'effectiveDate':
      return [GovukValidators.required('Enter the effective date of the notice'), effectiveDateMinValidator()];
    case 'annualEmissionsReportRequired':
      return [GovukValidators.required('Select Yes or No')];
    case 'annualEmissionsReportDate':
      return [GovukValidators.required('Enter the date when the revocation report is required')];
    case 'surrenderRequired':
      return [GovukValidators.required('Select Yes or No')];
    case 'surrenderDate':
      return [GovukValidators.required('Enter the date when the surrender of allowances is required')];
    case 'feeCharged':
      return [GovukValidators.required('Select Yes or No')];
    case 'feeDate':
      return [GovukValidators.required('Enter the date'), feeDateMinValidator()];
    case 'feeDetails':
      return [GovukValidators.required('Explain why payment is required')];
    default:
      return null;
  }
};
