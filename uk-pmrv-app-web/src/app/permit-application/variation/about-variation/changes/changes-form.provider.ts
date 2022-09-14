import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import {
  nonSignificantChanges,
  significantChangesMonitoringMethodologyPlan,
  significantChangesMonitoringPlan,
} from '../about-variation';

export const changesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getState();
    const modifications = state?.permitVariationDetails?.modifications;

    const changes = modifications?.map((change) => change.type);

    const otherNonSignficant = modifications?.find((change) => change.type === 'OTHER_NON_SIGNFICANT');
    const otherMonitoringPlan = modifications?.find((change) => change.type === 'OTHER_MONITORING_PLAN');
    const otherMonitoringMethodologyPlan = modifications?.find(
      (change) => change.type === 'OTHER_MONITORING_METHODOLOGY_PLAN',
    );

    return fb.group({
      nonSignificantChanges: [
        {
          value: changes?.filter((change) => Object.keys(nonSignificantChanges).includes(change)),
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a non-significant change'),
      ],
      otherNonSignficantSummary: [
        {
          value: otherNonSignficant?.otherSummary ?? null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required('Enter details of the other non-significant changes')],
      ],
      significantChangesMonitoringPlan: [
        {
          value: changes?.filter((change) => Object.keys(significantChangesMonitoringPlan).includes(change)),
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a significant change to the Monitoring Plan'),
      ],
      otherMonitoringPlanSummary: [
        {
          value: otherMonitoringPlan?.otherSummary ?? null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required('Enter details of the other significant modifications')],
      ],
      significantChangesMonitoringMethodologyPlan: [
        {
          value: changes?.filter((change) => Object.keys(significantChangesMonitoringMethodologyPlan).includes(change)),
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a significant change to the Monitoring Methodology Plan'),
      ],
      otherMonitoringMethodologyPlanSummary: [
        {
          value: otherMonitoringMethodologyPlan?.otherSummary ?? null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required('Enter details of the other significant modifications')],
      ],
    });
  },
};
