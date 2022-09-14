import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';

export const tierJustificationProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ): FormGroup => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const disabled = !state.isEditable;

    const subtaskData = getSubtaskData(state, index, route.snapshot.data.statusKey);

    return fb.group({
      justification: [
        {
          value: [
            subtaskData?.noHighestRequiredTierJustification?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            subtaskData?.noHighestRequiredTierJustification?.isTechnicallyInfeasible ? 'isTechnicallyInfeasible' : null,
          ].filter(Boolean),
          disabled,
        },
        GovukValidators.required('Select a justification'),
      ],
      technicalInfeasibilityExplanation: [
        {
          value: subtaskData?.noHighestRequiredTierJustification?.technicalInfeasibilityExplanation ?? null,
          disabled,
        },
        [
          GovukValidators.required('Explain why it is technically infeasible to meet the highest tier'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      files: requestTaskFileService.buildFormControl(
        store,
        subtaskData?.noHighestRequiredTierJustification?.files ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        disabled,
      ),
    });
  },
};
