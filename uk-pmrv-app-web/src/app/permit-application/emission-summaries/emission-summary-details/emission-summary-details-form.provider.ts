import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const emisionSummaryDetailsFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, route: ActivatedRoute) => {
    const emissionSummary = route.snapshot.paramMap.get('emissionSummaryIndex')
      ? store.permit.emissionSummaries[Number(route.snapshot.paramMap.get('emissionSummaryIndex'))]
      : null;

    return fb.group({
      sourceStream: [
        emissionSummary?.sourceStream &&
        store.permit.sourceStreams.some((stream) => stream.id === emissionSummary.sourceStream)
          ? emissionSummary.sourceStream
          : null,
        GovukValidators.required('Select a source stream'),
      ],
      emissionSources: [
        emissionSummary?.emissionSources
          ? emissionSummary.emissionSources.filter((source) =>
              store.permit.emissionSources.some((stateSource) => stateSource.id === source),
            )
          : null,
        GovukValidators.required('Select an emission source'),
      ],
      emissionPoints: [
        emissionSummary?.emissionPoints
          ? emissionSummary.emissionPoints.filter((point) =>
              store.permit.emissionPoints.some((statePoint) => statePoint.id === point),
            )
          : null,
        GovukValidators.required('Select an emission point'),
      ],
      regulatedActivity: [
        emissionSummary?.excludedRegulatedActivity
          ? 'excludedRegulatedActivity'
          : emissionSummary?.regulatedActivity &&
            store.permit.regulatedActivities.some((activity) => activity.id === emissionSummary.regulatedActivity)
          ? emissionSummary.regulatedActivity
          : null,
        GovukValidators.required('Select a related activity'),
      ],
    });
  },
};
