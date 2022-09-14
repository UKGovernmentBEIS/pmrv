import { InjectionToken } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../../shared/services/request-task-file-service/request-task-file.service';
import { RfiStore } from '../../store/rfi.store';

export const RFI_FORM = new InjectionToken<FormGroup>('Rfi form');

export const questionFormProvider = {
  provide: RFI_FORM,
  deps: [FormBuilder, RequestTaskFileService, RfiStore],
  useFactory: (fb: FormBuilder, requestTaskFileService: RequestTaskFileService, rfiStore: RfiStore) => {
    const questions = rfiStore.getValue().rfiSubmitPayload?.questions;
    const files = rfiStore.getValue().rfiSubmitPayload?.files || [];
    const deadline = rfiStore.getValue().rfiSubmitPayload?.deadline;

    return fb.group({
      questions: fb.array(questions?.length > 0 ? questions.map(createAnotherQuestion) : [createAnotherQuestion()]),
      deadline: [deadline, GovukValidators.required('Enter a date')],
      files: requestTaskFileService.buildFormControl(rfiStore, files ?? [], 'rfiAttachments', 'RFI_UPLOAD_ATTACHMENT'),
    });
  },
};

export function createAnotherQuestion(question?: string): FormGroup {
  return new FormGroup({
    question: new FormControl(question, [
      GovukValidators.required('Enter a question'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
