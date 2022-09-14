import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { combineLatest, first, map, mapTo } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { AuthService } from '../core/services/auth.service';
import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { taskNotFoundError } from '../shared/errors/concurrency-error';
import { SharedStore } from '../shared/store/shared.store';
import {
  initializePermitSectionsCompleted,
  initializeReviewSectionsCompleted,
} from './shared/permit-sections/initialize-sections';
import { PermitApplicationStore } from './store/permit-application.store';
import { getVariationRequestTaskTypes } from './variation/variation-types';

@Injectable({ providedIn: 'root' })
export class PermitApplicationGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: PermitApplicationStore,
    private readonly sharedStore: SharedStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly authService: AuthService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return combineLatest([
      this.tasksService.getTaskItemInfoByIdUsingGET(Number(route.paramMap.get('taskId'))),
      this.authService.userStatus,
    ]).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      first(),
      map(([requestTaskItem, userStatus]) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...requestTaskItem.requestTask.payload,
          requestTaskId: Number(route.paramMap.get('taskId')),
          requestId: requestTaskItem.requestInfo.id,
          isRequestTask: true,
          isVariation: getVariationRequestTaskTypes().some((type) => requestTaskItem.requestTask.type.includes(type)),
          requestTaskType: requestTaskItem.requestTask.type,
          isEditable: [
            'PERMIT_ISSUANCE_SAVE_APPLICATION',
            'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW',
            'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND',
            'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION',
            'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
          ].some((type) => requestTaskItem.allowedRequestTaskActions.includes(type)),
          assignee: {
            assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
            assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
          },
          userAssignCapable: requestTaskItem.userAssignCapable,
          competentAuthority: requestTaskItem.requestInfo.competentAuthority,
          accountId: requestTaskItem.requestInfo.accountId,
          daysRemaining: requestTaskItem.requestTask.daysRemaining,
          permit: {
            ...state.permit,
            ...requestTaskItem.requestTask.payload?.permit,
          },
          assignable: requestTaskItem.requestTask.assignable,
          allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          userViewRole: userStatus.roleType,
          permitSectionsCompleted: {
            ...(Object.keys(requestTaskItem.requestTask.payload?.permitSectionsCompleted ?? {})?.length === 0 &&
            ['PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT'].some((type) =>
              requestTaskItem.requestTask.type.includes(type),
            )
              ? initializePermitSectionsCompleted(requestTaskItem.requestTask.payload?.permit)
              : requestTaskItem.requestTask.payload?.permitSectionsCompleted),
          },
          reviewSectionsCompleted: {
            ...(Object.keys(requestTaskItem.requestTask.payload?.reviewSectionsCompleted ?? {})?.length === 0 &&
            ['PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT'].some((type) =>
              requestTaskItem.requestTask.type.includes(type),
            )
              ? initializeReviewSectionsCompleted(requestTaskItem.requestTask.payload?.permit)
              : requestTaskItem.requestTask.payload?.reviewSectionsCompleted),
          },
        });
        this.store.listenToStorage();

        this.sharedStore.reset();
        this.sharedStore.setState({ ...this.sharedStore.getState(), accountId: requestTaskItem.requestInfo.accountId });
      }),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.store.stopListeningToStorage();
    this.sharedStore.reset();

    return true;
  }
}
