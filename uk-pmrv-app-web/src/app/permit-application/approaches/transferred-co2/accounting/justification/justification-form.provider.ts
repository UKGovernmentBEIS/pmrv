import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const justificationFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const justification = (state.permit.monitoringApproaches?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
      .accountingEmissions?.accountingEmissionsDetails?.noHighestRequiredTierJustification;

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
      ),
    });
  },
};
