import { FormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const additionalDocumentsFormFactory = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();

    const item = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      ?.additionalDocuments;

    return fb.group({
      exist: [
        { value: item?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      documents: requestTaskFileService.buildFormControl(
        store,
        item?.documents ?? [],
        'aerAttachments',
        'AER_UPLOAD_SECTION_ATTACHMENT',
        true,
        !state.isEditable,
      ),
    });
  },
};
