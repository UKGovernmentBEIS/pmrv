import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/concurrency-error';
import { SharedStore } from '@shared/store/shared.store';

import { TasksService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { RdeStore } from './store/rde.store';

@Injectable({ providedIn: 'root' })
export class RdeGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: RdeStore,
    private readonly sharedStore: SharedStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.tasksService.getTaskItemInfoByIdUsingGET(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...requestTaskItem.requestTask.payload,
          requestTaskId: requestTaskItem.requestTask.id,
          assignee: {
            assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
            assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
          },
          requestId: requestTaskItem.requestInfo.id,
          accountId: requestTaskItem.requestInfo.accountId,
          daysRemaining: requestTaskItem.requestTask.daysRemaining,
          requestType: requestTaskItem.requestInfo.type,
          isEditable: ['RDE_SUBMIT', 'RDE_RESPONSE_SUBMIT', 'RDE_FORCE_DECISION'].some((perm) =>
            requestTaskItem.allowedRequestTaskActions.includes(perm as any),
          ),
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
