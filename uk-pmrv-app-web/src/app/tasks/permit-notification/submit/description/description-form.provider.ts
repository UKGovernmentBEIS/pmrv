import { FormBuilder, FormGroup } from '@angular/forms';

import {
  NonSignificantChange,
  OtherFactor,
  PermitNotificationApplicationSubmitRequestTaskPayload,
  TemporaryChange,
  TemporaryFactor,
  TemporarySuspension,
} from 'pmrv-api';

import { RequestTaskFileService } from '../../../../shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { endDateConditionalValidator, endDateValidator } from '../../core/permit-notification.validator';
import { PERMIT_NOTIFICATION_TASK_FORM } from '../../core/permit-notification-task-form.token';
import { NonSignificantChangeComponent } from '../../shared/components/non-significant-change/non-significant-change.component';
import { OtherFactorComponent } from '../../shared/components/other-factor/other-factor.component';
import { TemporaryChangeComponent } from '../../shared/components/temporary-change/temporary-change.component';
import { TemporaryFactorComponent } from '../../shared/components/temporary-factor/temporary-factor.component';
import { TemporarySuspensionComponent } from '../../shared/components/temporary-suspension/temporary-suspension.component';

export const descriptionFormProvider = {
  provide: PERMIT_NOTIFICATION_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService): FormGroup => {
    const state = store.getValue();

    const item = state.requestTaskItem.requestTask.payload as PermitNotificationApplicationSubmitRequestTaskPayload;
    const disabled = !state.isEditable;

    return fb.group({
      notification: fb.group(
        {
          ...createFormByType(item.permitNotification, disabled),
          documents: requestTaskFileService.buildFormControl(
            store,
            item.permitNotification?.documents ?? [],
            'permitNotificationAttachments',
            'PERMIT_NOTIFICATION_UPLOAD_ATTACHMENT',
            false,
            !state.isEditable,
          ),
        },
        {
          validators:
            item.permitNotification.type === 'NON_SIGNIFICANT_CHANGE'
              ? []
              : item.permitNotification.type === 'OTHER_FACTOR'
              ? [endDateConditionalValidator()]
              : [endDateValidator()],
        },
      ),
    });
  },
};

function createFormByType(
  value: TemporaryFactor | TemporaryChange | TemporarySuspension | NonSignificantChange | OtherFactor,
  disabled: boolean,
): any {
  switch (value.type) {
    case 'TEMPORARY_FACTOR':
      return TemporaryFactorComponent.controlsFactory(value as TemporaryFactor, disabled);
    case 'TEMPORARY_CHANGE':
      return TemporaryChangeComponent.controlsFactory(value as TemporaryChange, disabled);
    case 'TEMPORARY_SUSPENSION':
      return TemporarySuspensionComponent.controlsFactory(value as TemporarySuspension, disabled);
    case 'NON_SIGNIFICANT_CHANGE':
      return NonSignificantChangeComponent.controlsFactory(value as NonSignificantChange, disabled);
    case 'OTHER_FACTOR':
      return OtherFactorComponent.controlsFactory(value as OtherFactor, disabled);
  }
}
