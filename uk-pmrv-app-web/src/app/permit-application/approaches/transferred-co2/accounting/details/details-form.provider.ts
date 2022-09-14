import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const detailsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
      .accountingEmissions?.accountingEmissionsDetails;

    return fb.group({
      measurementDevicesOrMethods: [
        {
          value: value?.measurementDevicesOrMethods
            ? value?.measurementDevicesOrMethods.filter((item) =>
                store.permit.measurementDevicesOrMethods.some((stateItem) => stateItem.id === item),
              )
            : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select at least one measurement device'),
      ],
      samplingFrequency: [
        {
          value: value?.samplingFrequency ?? null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a frequency'),
      ],
      otherSamplingFrequency: [
        {
          value: value?.samplingFrequency === 'OTHER' ? value.otherSamplingFrequency : null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(50, 'Enter up to 50 characters')],
      ],
      tier: [
        {
          value: value?.tier ?? null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a tier'),
      ],
      noTierJustification: [
        {
          value: value?.tier === 'NO_TIER' ? value.noTierJustification : null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter how emissions from transferred CO2 will be estimated'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      isHighestRequiredTierT3: [
        {
          value: value?.tier === 'TIER_3' ? value.isHighestRequiredTier : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select Yes or No'),
      ],
      isHighestRequiredTierT2: [
        {
          value: value?.tier === 'TIER_2' ? value.isHighestRequiredTier : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select Yes or No'),
      ],
      isHighestRequiredTierT1: [
        {
          value: value?.tier === 'TIER_1' ? value.isHighestRequiredTier : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select Yes or No'),
      ],
    });
  },
};
