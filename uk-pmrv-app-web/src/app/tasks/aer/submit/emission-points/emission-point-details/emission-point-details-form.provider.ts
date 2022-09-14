import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { IdGeneratorService } from '@shared/services/id-generator.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const emissionPointDetailsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute, IdGeneratorService],
  useFactory: (
    fb: FormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    idGeneratorService: IdGeneratorService,
  ) => {
    const state = store.getValue();
    const aerSubmitPayload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
    const emissionPoint = aerSubmitPayload.aer?.emissionPoints?.find(
      (ep) => ep.id === route.snapshot.paramMap.get('emissionPointId'),
    );

    return fb.group({
      id: [emissionPoint?.id ?? idGeneratorService.generateId()],
      reference: [emissionPoint?.reference, GovukValidators.required('Enter a reference')],
      description: [emissionPoint?.description, GovukValidators.required('Enter a description')],
    });
  },
};
