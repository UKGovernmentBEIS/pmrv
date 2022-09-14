import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CellAndAnodeType, PFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const typesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const value = store.permit.monitoringApproaches?.PFC as PFCMonitoringApproach;

    return fb.group({
      cellAndAnodeTypes: fb.array(
        value?.cellAndAnodeTypes?.length > 0
          ? value?.cellAndAnodeTypes?.map((val) => createCellAndAnodeTypes(val, !store.getValue().isEditable))
          : [createCellAndAnodeTypes(null, !store.getValue().isEditable)],
      ),
    });
  },
};

export function createCellAndAnodeTypes(value?: CellAndAnodeType, disabled = false): FormGroup {
  return new FormGroup({
    cellType: new FormControl({ value: value?.cellType ?? null, disabled }, [
      GovukValidators.required('Enter a cell type'),
      GovukValidators.maxLength(100, 'Enter up to 100 characters'),
    ]),
    anodeType: new FormControl({ value: value?.anodeType ?? null, disabled }, [
      GovukValidators.required('Enter an anode type'),
      GovukValidators.maxLength(100, 'Enter up to 100 characters'),
    ]),
  });
}
