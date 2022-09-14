import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';

import { first, mapTo, Observable, tap } from 'rxjs';

import {
  initializePermitSectionsCompleted,
  initializeReviewSectionsCompleted,
} from '../../permit-application/shared/permit-sections/initialize-sections';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { CommonTasksStore } from '../store/common-tasks.store';

@Injectable({
  providedIn: 'root',
})
export class PermitVariationGuard implements CanActivate {
  constructor(
    private readonly commonTasksStore: CommonTasksStore,
    private readonly permitApplicationStore: PermitApplicationStore,
  ) {}

  canActivate(): Observable<boolean> {
    return this.commonTasksStore.pipe(
      first(),
      tap((state) => {
        const variationPayload = state.requestTaskItem.requestTask.payload as any;
        this.permitApplicationStore.setState({
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskType: state.requestTaskItem.requestTask.type,
          isRequestTask: true,
          isVariation: true,
          isEditable: [
            'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION',
            'PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION',
          ].some((type) => state.requestTaskItem.allowedRequestTaskActions.includes(type as any)),
          assignable: false, //explicitly false so as to hide the assignee section (available through new task component)
          userAssignCapable: false, //explicitly false so as to hide the assignee section (available through new task component)
          assignee: undefined, //explicitly undefined
          reviewGroupDecisions: undefined, //not applicable
          competentAuthority: state.requestTaskItem.requestInfo.competentAuthority,
          accountId: state.requestTaskItem.requestInfo.accountId,
          daysRemaining: state.requestTaskItem.requestTask.daysRemaining,
          ...variationPayload,
          permitSectionsCompleted: {
            ...(Object.keys(variationPayload?.permitSectionsCompleted).length === 0
              ? initializePermitSectionsCompleted(variationPayload?.permit)
              : variationPayload.permitSectionsCompleted),
          },
          reviewSectionsCompleted: {
            ...(Object.keys(variationPayload?.reviewSectionsCompleted).length === 0
              ? initializeReviewSectionsCompleted(variationPayload?.permit)
              : variationPayload.reviewSectionsCompleted),
          },
        });
      }),
      mapTo(true),
    );
  }
}
