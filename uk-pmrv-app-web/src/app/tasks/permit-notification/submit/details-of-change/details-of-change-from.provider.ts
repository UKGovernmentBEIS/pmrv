import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitNotificationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PERMIT_NOTIFICATION_TASK_FORM } from '../../core/permit-notification-task-form.token';

export const detailsOfChangeFromProvider = {
  provide: PERMIT_NOTIFICATION_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore): FormGroup => {
    const state = store.getValue();

    const item = state?.requestTaskItem?.requestTask?.payload as PermitNotificationApplicationSubmitRequestTaskPayload;
    const disabled = !state.isEditable;

    return fb.group({
      type: [
        { value: item?.permitNotification?.type ?? null, disabled },
        GovukValidators.required('Select a notification type'),
      ],
    });
  },
};
