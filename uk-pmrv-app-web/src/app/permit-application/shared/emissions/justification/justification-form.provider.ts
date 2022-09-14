import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MeasMonitoringApproach, N2OMonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../shared/services/request-task-file-service/request-task-file.service';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const JUSTIFICATION_FORM = new InjectionToken<FormGroup>('Justification form');

export const justificationFormProvider = {
  provide: JUSTIFICATION_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const { taskKey } = route.snapshot.data;

    const state = store.getValue();

    const justification =
      (state.permit.monitoringApproaches[taskKey] as N2OMonitoringApproach | MeasMonitoringApproach)
        .sourceStreamCategoryAppliedTiers?.[index]?.measuredEmissions?.noHighestRequiredTierJustification ?? null;

    return fb.group({
      justification: [
        {
          value: [
            justification?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            justification?.isTechnicallyInfeasible ? 'isTechnicallyInfeasible' : null,
          ].filter(Boolean),
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a justification'),
      ],
      technicalInfeasibilityExplanation: [
        {
          value: justification?.technicalInfeasibilityExplanation ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Explain why it is technically infeasible to meet the highest tier'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      files: requestTaskFileService.buildFormControl(
        store,
        justification?.files ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
