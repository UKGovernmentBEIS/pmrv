import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { IdGeneratorService } from '@shared/services/id-generator.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const sourceStreamFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute, IdGeneratorService],
  useFactory: (
    fb: FormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    idGeneratorService: IdGeneratorService,
  ) => {
    const state = store.getValue();
    const sourceStream = (
      state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
    ).aer?.sourceStreams?.find((stream) => stream.id === route.snapshot.paramMap.get('streamId'));

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
