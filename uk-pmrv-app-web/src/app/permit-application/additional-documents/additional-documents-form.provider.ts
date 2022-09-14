import { FormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const additionalDocumentsFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, requestTaskFileService: RequestTaskFileService) => {
    const value = store.permit.additionalDocuments;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ],
      documents: requestTaskFileService.buildFormControl(
        store,
        value?.documents ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        true,
      ),
    });
  },
};
