import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

export const uploadDocumentsFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [FormBuilder, PermitSurrenderStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    store: PermitSurrenderStore,
    requestTaskFileService: RequestTaskFileService,
  ): FormGroup => {
    const state = store.getValue();
    const documents = state.permitSurrender?.documents;

    const disabled = !state.isEditable;

    return fb.group({
      documents: requestTaskFileService.buildFormControl(
        store,
        documents ?? [],
        'permitSurrenderAttachments',
        'PERMIT_SURRENDER_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
    });
  },
};
