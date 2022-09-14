import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { MonitoringRole } from 'pmrv-api';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const monitoringRolesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore, requestTaskFileService: RequestTaskFileService) => {
    const value = store.permit.monitoringReporting;

    return fb.group({
      monitoringRoles: fb.array(
        value?.monitoringRoles.length > 0
          ? value.monitoringRoles.map((v) => createAnotherRole(v, !store.getValue().isEditable))
          : [createAnotherRole(null, !store.getValue().isEditable)],
      ),
      organisationCharts: requestTaskFileService.buildFormControl(
        store,
        value?.organisationCharts ?? [],
        'permitAttachments',
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !store.getValue().isEditable,
      ),
    });
  },
};

export function createAnotherRole(value?: MonitoringRole, disabled = false): FormGroup {
  return new FormGroup({
    jobTitle: new FormControl({ value: value?.jobTitle ?? null, disabled }, [
      GovukValidators.required('Enter a job title'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    mainDuties: new FormControl({ value: value?.mainDuties ?? null, disabled }, [
      GovukValidators.required('Enter the main duties'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
