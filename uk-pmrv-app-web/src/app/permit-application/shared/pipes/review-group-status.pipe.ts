import { Pipe, PipeTransform } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { findTasksByReviewGroupName, ReviewGroupStatus } from '../../review/review';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { StatusKey } from '../types/permit-task.type';
import { TaskStatusPipe } from './task-status.pipe';

@Pipe({
  name: 'reviewGroupStatus',
})
export class ReviewGroupStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ): Observable<ReviewGroupStatus> {
    const taskStatus = new TaskStatusPipe(this.store);
    const groupTasks = findTasksByReviewGroupName(key);

    const groupTaskStatuses = [];
    groupTasks && groupTasks.forEach((task) => groupTaskStatuses.push(taskStatus.transform(task as StatusKey)));

    return combineLatest([this.store, ...groupTaskStatuses]).pipe(
      map(([state, ...groupTaskStatuses]: any) => {
        if (state.requestActionType === 'PERMIT_ISSUANCE_APPLICATION_GRANTED') {
          return 'accepted';
        }

        if (groupTaskStatuses?.length > 0 && !groupTaskStatuses.includes('in progress')) {
          return groupTaskStatuses.includes('needs review')
            ? 'needs review'
            : state.reviewSectionsCompleted?.[key] && state.reviewGroupDecisions?.[key]?.type === 'ACCEPTED'
            ? 'accepted'
            : state.reviewSectionsCompleted?.[key] && state.reviewGroupDecisions?.[key]?.type === 'REJECTED'
            ? 'rejected'
            : state.reviewSectionsCompleted?.[key] &&
              state.reviewGroupDecisions?.[key]?.type === 'OPERATOR_AMENDS_NEEDED'
            ? 'operator to amend'
            : 'undecided';
        }
        return 'undecided';
      }),
    );
  }
}
