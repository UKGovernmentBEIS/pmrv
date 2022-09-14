import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { IdGeneratorService } from '../../../shared/services/id-generator.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const sourceStreamFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute, IdGeneratorService],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    route: ActivatedRoute,
    idGeneratorService: IdGeneratorService,
  ) => {
    const sourceStream = store.permit.sourceStreams.find(
      (stream) => stream.id === route.snapshot.paramMap.get('streamId'),
    );

    return fb.group({
      id: [sourceStream?.id ?? idGeneratorService.generateId()],
      reference: [sourceStream?.reference ?? null, GovukValidators.required('Enter a reference')],
      description: [sourceStream?.description ?? null, GovukValidators.required('Select a description')],
      otherDescriptionName: [
        { value: sourceStream?.otherDescriptionName ?? null, disabled: sourceStream?.description !== 'OTHER' },
        GovukValidators.required('Enter a description'),
      ],
      type: [sourceStream?.type ?? null, GovukValidators.required('Select a type')],
    });
  },
};
