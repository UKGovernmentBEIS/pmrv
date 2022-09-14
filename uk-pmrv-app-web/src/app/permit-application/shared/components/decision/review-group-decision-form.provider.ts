import { InjectionToken } from '@angular/core';
import { AsyncValidatorFn, FormBuilder, FormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { findTasksByReviewGroupName, reviewGroupsTasks } from '../../../review/review';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { TaskStatusPipe } from '../../pipes/task-status.pipe';

export const REVIEW_GROUP_DECISION_FORM = new InjectionToken<FormGroup>('Appropriateness task form');

export const reviewGroupDecisionFormProvider = {
  provide: REVIEW_GROUP_DECISION_FORM,
  deps: [FormBuilder, PermitApplicationStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    store: PermitApplicationStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const groupKey = route.snapshot.data.groupKey;
    const reviewDecision = store.reviewGroupDecisions?.[groupKey];

    const taskStatus = new TaskStatusPipe(store);
    const groupTasks = findTasksByReviewGroupName(groupKey);
    const groupTaskStatuses = [];

    if (groupKey === 'MANAGEMENT_PROCEDURES' && !store.permit.managementProceduresExist) {
      groupTaskStatuses.push(taskStatus.transform(reviewGroupsTasks.MANAGEMENT_PROCEDURES[0]));
    } else {
      groupTasks && groupTasks.forEach((task) => groupTaskStatuses.push(taskStatus.transform(task)));
    }

    return fb.group(
      {
        decision: [reviewDecision?.type ?? null, GovukValidators.required('Select a decision for this review group')],
        notes: [
          reviewDecision?.notes ?? null,
          [
            GovukValidators.required('Enter notes for this review group'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        changesRequired: [
          {
            value: reviewDecision?.changesRequired ?? null,
            disabled: reviewDecision?.type !== 'OPERATOR_AMENDS_NEEDED',
          },
          [
            GovukValidators.required('Enter all the changes required for this review group'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        files: requestTaskFileService.buildFormControl(
          store,
          reviewDecision?.files ?? [],
          'reviewAttachments',
          store.getFileUploadReviewGroupDecisionAttachmentActionContext(),
          false,
          reviewDecision?.type !== 'OPERATOR_AMENDS_NEEDED',
        ),
      },
      {
        asyncValidators: subtasksCompleted(groupTaskStatuses),
        updateOn: 'change',
      },
    );
  },
};

function subtasksCompleted(statuses: any[]): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    combineLatest([...statuses]).pipe(
      first(),
      map(([...groupTaskStatuses]: any[]) =>
        groupTaskStatuses.some((status) => status !== 'complete')
          ? { atLeastOne: 'All sections must be completed' }
          : null,
      ),
    );
}
