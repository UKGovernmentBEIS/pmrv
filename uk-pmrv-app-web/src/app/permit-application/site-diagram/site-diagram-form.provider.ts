import { FormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const siteDiagramAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const value = state.permit.siteDiagrams;

    return fb.group({
      siteDiagrams: requestTaskFileService.buildFormControl(
        store,
        value ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
