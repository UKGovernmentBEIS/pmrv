import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const uploadMonitoringMethodologyFileProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    requestTaskFileService: RequestTaskFileService,
  ): FormGroup => {
    const state = store.getValue();
    const files = state.permit?.monitoringMethodologyPlans?.plans;

    const disabled = !state.isEditable;

    return fb.group({
      files: requestTaskFileService.buildFormControl(
        store,
        files ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        true,
        disabled,
      ),
    });
  },
};
