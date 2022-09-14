import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { RdeStore } from '../store/rde.store';

export const RDE_FORM = new InjectionToken<FormGroup>('Rde form');

export const forceDecisionProvider = {
  provide: RDE_FORM,
  deps: [FormBuilder, RequestTaskFileService, RdeStore],
  useFactory: (fb: FormBuilder, requestTaskFileService: RequestTaskFileService, store: RdeStore) => {
    const state = store.getState();
    const files = [];
    const disabled = !store.getValue().isEditable;

    return fb.group({
      decision: [{ value: null, disabled: !state.isEditable }, GovukValidators.required('Select a decision')],
      evidence: [
        { value: null, disabled: !state.isEditable },
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      files: requestTaskFileService.buildFormControl(
        store,
        files,
        'rdeAttachments',
        'RDE_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
