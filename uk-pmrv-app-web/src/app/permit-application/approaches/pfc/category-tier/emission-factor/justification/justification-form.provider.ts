import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { PFCMonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';

export const justificationFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const justification =
      (state.permit.monitoringApproaches?.PFC as PFCMonitoringApproach)?.sourceStreamCategoryAppliedTiers?.[index]
        ?.emissionFactor?.noHighestRequiredTierJustification ?? null;
    const disabled = !state.isEditable;

    return fb.group({
      justification: [
        {
          value: [
            justification?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            justification?.isTechnicallyInfeasible ? 'isTechnicallyInfeasible' : null,
          ].filter(Boolean),
          disabled,
        },
        GovukValidators.required('Select a justification'),
      ],
      technicalInfeasibilityExplanation: [
        { value: justification?.technicalInfeasibilityExplanation ?? null, disabled },
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
        disabled,
      ),
    });
  },
};
