import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

const getThreeDecimalPlacesValidator = GovukValidators.pattern(
  '-?[0-9]*\\.?[0-9]{0,3}',
  'Estimated tonnes of CO2e must be to 3 decimal places',
);

export const categoryFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const tiers = (state.permit.monitoringApproaches.PFC as PFCMonitoringApproach).sourceStreamCategoryAppliedTiers;
    const sourceStreamCategoryTier = tiers
      ? tiers[Number(route.snapshot.paramMap.get('index'))]?.sourceStreamCategory ?? null
      : null;

    return fb.group({
      sourceStream: [
        {
          value:
            sourceStreamCategoryTier?.sourceStream &&
            store.permit.sourceStreams.some((stream) => stream.id === sourceStreamCategoryTier.sourceStream)
              ? sourceStreamCategoryTier.sourceStream
              : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a source stream'),
      ],
      emissionSources: [
        {
          value: sourceStreamCategoryTier?.emissionSources
            ? sourceStreamCategoryTier.emissionSources.filter((source) =>
                store.permit.emissionSources.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select at least one emission source'),
      ],
      emissionPoints: [
        {
          value: sourceStreamCategoryTier?.emissionPoints
            ? sourceStreamCategoryTier.emissionPoints.filter((point) =>
                store.permit.emissionPoints.some((statePoint) => statePoint.id === point),
              )
            : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select at least one emission point'),
      ],
      annualEmittedCO2Tonnes: [
        {
          value: sourceStreamCategoryTier?.annualEmittedCO2Tonnes ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Select an estimated tonnes of CO2e'),
          GovukValidators.min(-99999999.999, 'Estimated tonnes of CO2e must be greater or equal than -99,999,999.999'),
          GovukValidators.max(99999999.999, 'Estimated tonnes of CO2e must be less or equal than 99,999,999.999'),
          getThreeDecimalPlacesValidator,
        ],
      ],
      calculationMethod: [
        {
          value: sourceStreamCategoryTier?.calculationMethod,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a calculation method'),
      ],
      categoryType: [
        {
          value: sourceStreamCategoryTier?.categoryType,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a category'),
      ],
    });
  },
};
