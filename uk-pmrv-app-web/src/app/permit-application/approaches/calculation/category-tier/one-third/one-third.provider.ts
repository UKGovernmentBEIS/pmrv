import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';

export const oneThirdProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ): FormGroup => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const disabled = !state.isEditable;

    const subtaskData = getSubtaskData(state, index, route.snapshot.data.statusKey);

    return fb.group({
      oneThirdRule: [
        { value: subtaskData?.oneThirdRule ?? null, disabled },
        GovukValidators.required('Select Yes or No'),
      ],
      oneThirdRuleFiles: requestTaskFileService.buildFormControl(
        store,
        subtaskData?.oneThirdRuleFiles ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        disabled,
      ),
    });
  },
};
