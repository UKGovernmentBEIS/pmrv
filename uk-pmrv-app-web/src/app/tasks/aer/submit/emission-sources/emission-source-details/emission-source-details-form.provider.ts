import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { IdGeneratorService } from '@shared/services/id-generator.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const emissionSourcesAddFormFactory = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute, IdGeneratorService],
  useFactory: (
    fb: FormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    idGeneratorService: IdGeneratorService,
  ) => {
    const source = (
      store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
    ).aer?.emissionSources?.find((emissionSource) => emissionSource.id === route.snapshot.paramMap.get('sourceId'));

    return fb.group({
      id: [source?.id ?? idGeneratorService.generateId()],
      reference: [source?.reference ?? null, GovukValidators.required('Enter a reference')],
      description: [source?.description ?? null, GovukValidators.required('Enter a description')],
    });
  },
};
