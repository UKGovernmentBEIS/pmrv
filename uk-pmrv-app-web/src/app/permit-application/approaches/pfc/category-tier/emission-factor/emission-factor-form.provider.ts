import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const emissionFactorFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();

    const emissionFactor =
      (state.permit.monitoringApproaches.PFC as PFCMonitoringApproach)?.sourceStreamCategoryAppliedTiers?.[index]
        ?.emissionFactor ?? null;
    const disabled = !state.isEditable;

    return fb.group({
      tier: [{ value: emissionFactor?.tier ?? null, disabled }, GovukValidators.required('Select a tier')],
      isHighestRequiredTierT1: [
        {
          value: emissionFactor?.tier === 'TIER_1' ? emissionFactor.isHighestRequiredTier : null,
          disabled: disabled || emissionFactor?.tier !== 'TIER_2',
        },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
    });
  },
};
