import { InjectionToken } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

export const PERMIT_REVOCATION_WITHDRAW_FORM = new InjectionToken<FormGroup>('Permit revocation withdraw form');
export const withdrawFormProvider = {
  provide: PERMIT_REVOCATION_WITHDRAW_FORM,
  deps: [FormBuilder, PermitRevocationStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    store: PermitRevocationStore,
    requestTaskFileService: RequestTaskFileService,
  ): FormGroup => {
    const state = store.getValue();
    const files = state.withdrawFiles;

    const disabled = !state.isEditable;
    return fb.group(
      {
        reason: [{ value: state.reason ?? null, disabled }, { validators: GovukValidators.required('Enter a reason') }],
        files: requestTaskFileService.buildFormControl(
          store,
          files ?? [],
          'revocationAttachments',
          'PERMIT_REVOCATION_UPLOAD_ATTACHMENT',
          false,
          disabled,
        ),
      },
      { updateOn: 'change' },
    );
  },
};
